package com.g3.launcher.manager

import com.g3.launcher.Constants
import com.g3.launcher.util.httpClient
import com.g3.launcher.util.largeFileHttpClient
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import java.io.File
import java.io.RandomAccessFile
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

/**
 * Работа с пакетами установки
 */
object PackagesManager {
    private const val REPOSITORY_OWNER = "1lio"
    private const val REPOSITORY_NAME = "g3_launcher"

    private const val BASE_PATH: String = "app/resources/base.zip"
    private const val EN_PATH: String = "app/resources/en.zip"
    private const val DE_PATH: String = "app/resources/de.zip"
    private const val FR_PATH: String = "app/resources/fr.zip"
    private const val PL_PATH: String = "app/resources/pl.zip"
    private const val RU_PATH: String = "app/resources/ru.zip"

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val downloadMutex = Mutex()

    fun getAvailablePackages(): List<String> {
        val list = mutableListOf<String>()

        if (File(BASE_PATH).exists()) {
            list.add("base")
        }

        if (File(EN_PATH).exists()) {
            list.add("en")
        }

        if (File(DE_PATH).exists()) {
            list.add("de")
        }

        if (File(FR_PATH).exists()) {
            list.add("fr")
        }

        if (File(PL_PATH).exists()) {
            list.add("pl")
        }

        if (File(RU_PATH).exists()) {
            list.add("ru")
        }

        return list
    }

    suspend fun installBasePackage(onProgress: (Int) -> Unit = {}): Int {
        val downloadUrl = "https://github.com/$REPOSITORY_OWNER/$REPOSITORY_NAME/releases/download/${Constants.LAUNCHER_VERSION}/base.zip"
        return downloadMutex.withLock {
            val targetFile = File(BASE_PATH)

            // Проверяем существование файла и его целостность
            if (targetFile.exists() && targetFile.length() > 0) {
                if (verifyFileIntegrity(BASE_PATH, getFileSize(downloadUrl))) {
                    return@withLock 100
                }
            }

            try {
                targetFile.parentFile?.mkdirs()

                println("Начинаем загрузку base.zip из: $downloadUrl")

                downloadLargeFileWithResume(downloadUrl, targetFile, onProgress)

                return if (targetFile.exists() && targetFile.length() > 0) {
                    println("base.zip успешно загружен (${targetFile.length() / 1024 / 1024} MB)")
                    100
                } else {
                    0
                }
            } catch (e: Exception) {
                println("Ошибка при загрузке base.zip: ${e.message}")
                e.printStackTrace()
                return@withLock 0
            }
        }
    }

    private suspend fun downloadFileWithProgress(
        url: String,
        targetFile: File,
        onProgress: (Int) -> Unit = {}
    ) {
        val response = largeFileHttpClient.get(url) {
            expectSuccess = false

            onDownload { bytesSentTotal, contentLength ->
                if (contentLength != null) {
                    val progress = (bytesSentTotal.toFloat() / contentLength * 100).roundToInt()
                    onProgress(progress.coerceIn(0, 100))
                }
            }
        }

        if (!response.status.isSuccess()) {
            throw Exception("Не удалось скачать файл: ${response.status}")
        }


        response.bodyAsChannel().copyAndClose(targetFile.writeChannel())
    }

    /**
     * Улучшенная загрузка больших файлов с поддержкой возобновления
     */
    private suspend fun downloadLargeFileWithResume(
        url: String,
        targetFile: File,
        onProgress: (Int) -> Unit = {}
    ) {
        var downloadedBytes = 0L
        val totalBytes = getFileSize(url) // Пытаемся получить размер

        // Если файл уже частично скачан, пробуем возобновить
        if (targetFile.exists()) {
            downloadedBytes = targetFile.length()
            if (downloadedBytes > 0 && totalBytes != null && downloadedBytes < totalBytes) {
                println("Возобновляем загрузку с ${downloadedBytes / 1024 / 1024} MB")
            }
        }

        val connection = URL(url).openConnection() as HttpURLConnection
        connection.setRequestProperty("Range", "bytes=$downloadedBytes-")
        connection.connectTimeout = 60000
        connection.readTimeout = 300000

        if (connection.responseCode != HttpURLConnection.HTTP_PARTIAL &&
            connection.responseCode != HttpURLConnection.HTTP_OK
        ) {
            throw Exception("HTTP ошибка: ${connection.responseCode}")
        }

        RandomAccessFile(targetFile, "rw").use { raf ->
            raf.seek(downloadedBytes)

            connection.inputStream.use { input ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var lastProgress = -1

                while (input.read(buffer).also { bytesRead = it } != -1) {
                    raf.write(buffer, 0, bytesRead)
                    downloadedBytes += bytesRead

                    if (totalBytes != null) {
                        val progress = (downloadedBytes.toFloat() / totalBytes * 100).roundToInt()
                        if (progress != lastProgress) {
                            onProgress(progress)
                            lastProgress = progress
                        }
                    }

                    // Периодически сохраняем прогресс
                    if (downloadedBytes % (10 * 1024 * 1024) == 0L) { // Каждые 10 MB
                        println("Загружено: ${downloadedBytes / 1024 / 1024} MB")
                    }
                }
            }
        }

        connection.disconnect()
    }

    /**
     * Получает размер файла по URL
     */
    private suspend fun getFileSize(url: String): Long? {
        return try {
            withTimeout(30000) {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "HEAD"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val contentLength = connection.contentLengthLong
                connection.disconnect()

                if (contentLength > 0) contentLength else null
            }
        } catch (e: Exception) {
            println("Не удалось получить размер файла: ${e.message}")
            null
        }
    }

    suspend fun installLanguagePackage(language: String, onProgress: (Int) -> Unit = {}): Int {
        return downloadMutex.withLock {
            val filePath = when (language.lowercase()) {
                "en" -> EN_PATH
                "de" -> DE_PATH
                "fr" -> FR_PATH
                "pl" -> PL_PATH
                "ru" -> RU_PATH
                else -> throw IllegalArgumentException("Неподдерживаемый язык: $language")
            }

            val targetFile = File(filePath)
            if (targetFile.exists() && targetFile.length() > 0) {
                return@withLock 100
            }

            try {
                targetFile.parentFile?.mkdirs()
                val downloadUrl = if (Constants.LAUNCHER_VERSION.isNotEmpty()) {
                    "https://github.com/$REPOSITORY_OWNER/$REPOSITORY_NAME/releases/download/${Constants.LAUNCHER_VERSION}/${language}.zip"
                } else {
                    "https://github.com/$REPOSITORY_OWNER/$REPOSITORY_NAME/releases/latest/download/${language}.zip"
                }

                println("Загружаем $language.zip...")
                downloadFileWithProgress(downloadUrl, targetFile, onProgress)

                return if (targetFile.exists() && targetFile.length() > 0) {
                    100
                } else {
                    0
                }
            } catch (e: Exception) {
                println("Ошибка при загрузке $language.zip: ${e.message}")
                return@withLock 0
            }
        }
    }

    /**
     * Проверяет целостность скачанного файла
     */
    suspend fun verifyFileIntegrity(filePath: String, expectedSize: Long? = null): Boolean {
        return try {
            val file = File(filePath)
            if (!file.exists()) return false

            if (expectedSize != null && file.length() != expectedSize) {
                println("Неверный размер файла: ${file.length()} вместо $expectedSize")
                return false
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun dispose() {
        scope.cancel()
        largeFileHttpClient.close()
    }
}
