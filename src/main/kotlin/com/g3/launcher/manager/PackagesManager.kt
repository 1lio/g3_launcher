package com.g3.launcher.manager

import com.g3.launcher.Constants.LAUNCHER_VERSION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.RandomAccessFile
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.math.roundToInt

/**
 * Работа с пакетами установки
 */
object PackagesManager {

    sealed class Package(val key: String, val path: String, val length: Long, val countFiles: Int) {
        object Base : Package("base", "app/resources/base.zip", 1843983914L, 55)
        object En : Package("en", "app/resources/en.zip", 616635412L, 1)
        object De : Package("de", "app/resources/de.zip", 624737587L, 1)
        object Fr : Package("fr", "app/resources/fr.zip", 609685604L, 1)
        object Pl : Package("pl", "app/resources/pl.zip", 916613129L, 1)
        object Ru : Package("ru", "app/resources/ru.zip", 1364683928L, 2)
    }

    private const val BASE_URL: String = "https://github.com/1lio/g3_launcher/releases/download/$LAUNCHER_VERSION"

    private val PACKAGES = listOf(
        Package.Base,
        Package.En,
        Package.De,
        Package.Fr,
        Package.Pl,
        Package.Ru,
    )

    fun getAvailablePackages(): List<String> {
        return buildList {
            PACKAGES.forEach {
                if (verifyPackage(it.key)) {
                    add(it.key)
                }
            }
        }
    }

    fun verifyPackage(key: String): Boolean {
        val pack = PACKAGES.find { it.key == key } ?: return false
        val file = File(pack.path)
        return file.exists() && file.length() == pack.length
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val baseMutex = Mutex()
    private val languageMutexes = mutableMapOf<String, Mutex>() // Язык -> Mutex
    private val downloadJobs = mutableMapOf<String, Job>()
    private val downloadCallbacks = mutableMapOf<String, MutableList<(Int) -> Unit>>()

    suspend fun downloadBasePackage(onProgress: suspend (Int) -> Unit) {
        val downloadUrl = "$BASE_URL/base.zip"
        val pack = Package.Base

        if (verifyPackage(pack.key)) {
            onProgress(100)
            return
        }

        baseMutex.withLock {
            try {
                val targetFile = File(pack.path)
                downloadLargeFileWithResume(
                    url = downloadUrl,
                    targetFile = targetFile,
                    onProgress = onProgress
                )
            } catch (e: Exception) {
                println("Ошибка при загрузке base.zip: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    suspend fun extractBasePackage(
        gameDirPath: String,
        onProgress: suspend (percent: Int, count: Int, total: Int) -> Unit
    ) {
        val gameDir = File(gameDirPath)
        if (!gameDir.exists()) return

        try {
            val pack = Package.Base
            val file = File(pack.path)
            file.inputStream().use { input ->
                extractZipArchive(
                    inputStream = input,
                    outputDir = gameDir,
                    totalFiles = pack.countFiles,
                    totalBytes = pack.length,
                    onProgress = onProgress,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun downloadLocalization(language: String, onProgress: (Int) -> Unit) {
        val callbacks = downloadCallbacks.getOrPut(language) { mutableListOf() }
        callbacks.add(onProgress)

        if (downloadJobs[language]?.isActive == true) return

        val job = scope.launch {
            downloadLanguagePackage(language) { progress ->
                // уведомляем всех подписчиков
                downloadCallbacks[language]?.forEach { it(progress) }
            }

            // по завершении
            downloadCallbacks[language]?.forEach { it(100) }
            downloadJobs.remove(language)
            downloadCallbacks.remove(language)
        }

        downloadJobs[language] = job
    }

    suspend fun extractLocalizationPackage(
        localizationKey: String,
        gameDirPath: String,
        onProgress: suspend (percent: Int, count: Int, total: Int) -> Unit
    ) {
        val gameDir = File(gameDirPath)
        if (!gameDir.exists()) return

        try {
            val pack = PACKAGES.find { it.key == localizationKey  } ?: return
            val file = File(pack.path)
            file.inputStream().use { input ->
                extractZipArchive(
                    inputStream = input,
                    outputDir = gameDir,
                    totalFiles = pack.countFiles,
                    totalBytes = pack.length,
                    onProgress = onProgress,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun stopDownload(language: String) {
        downloadJobs[language]?.cancel()
        downloadJobs.remove(language)
        downloadCallbacks.remove(language)
    }

    private suspend fun downloadLanguagePackage(
        language: String,
        onProgress: suspend (Int) -> Unit = {}
    ): Int {
        val filePath = PACKAGES.find { it.key == language }?.path ?: return 0

        val languageMutex = languageMutexes.getOrPut(language) { Mutex() }

        return languageMutex.withLock {
            val downloadUrl = "$BASE_URL/${language}.zip"

            val targetFile = File(filePath)
            if (targetFile.exists() && targetFile.length() > 0) {
                if (verifyFileIntegrity(filePath, getFileSize(downloadUrl))) {
                    return@withLock 100
                }
            }

            try {
                targetFile.parentFile?.mkdirs()

                println("Загружаем $language.zip...")
                downloadLargeFileWithResume(downloadUrl, targetFile, onProgress)

                return if (targetFile.exists() && targetFile.length() > 0) {
                    println("$language.zip успешно загружен (${targetFile.length() / 1024} KB)")
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
     * Улучшенная загрузка больших файлов с поддержкой возобновления
     */
    private suspend fun downloadLargeFileWithResume(
        url: String,
        targetFile: File,
        onProgress: suspend (Int) -> Unit = {}
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

        try {
            RandomAccessFile(targetFile, "rw").use { raf ->
                raf.seek(downloadedBytes)

                connection.inputStream.use { input ->
                    val buffer = ByteArray(8192)
                    var lastProgress = -1

                    while (true) {
                        currentCoroutineContext().ensureActive()

                        val read = input.read(buffer)
                        if (read == -1) break

                        raf.write(buffer, 0, read)
                        downloadedBytes += read

                        if (totalBytes != null) {
                            val progress = (downloadedBytes.toFloat() / totalBytes * 100).roundToInt()
                            if (progress != lastProgress) {
                                onProgress(progress)
                                lastProgress = progress
                            }
                        }
                    }
                }
            }
        } finally {
            connection.disconnect()
        }
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


    /**
     * Проверяет целостность скачанного файла
     */
    fun verifyFileIntegrity(filePath: String, expectedSize: Long? = null): Boolean {
        return try {
            val file = File(filePath)
            if (!file.exists()) return false

            if (expectedSize != null && file.length() != expectedSize) {
                println("Неверный размер файла: ${file.length()} вместо $expectedSize")
                return false
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun extractZipArchive(
        inputStream: InputStream,
        outputDir: File,
        totalFiles: Int,
        totalBytes: Long,
        onProgress: suspend (percent: Int, extractedFiles: Int, totalFiles: Int) -> Unit
    ) {
        val buffer = ByteArray(64 * 1024) // 64 KB — оптимально

        var extractedBytes = 0L
        var extractedFiles = 0
        var lastPercent = -1

        ZipInputStream(BufferedInputStream(inputStream)).use { zis ->
            var entry: ZipEntry?

            while (zis.nextEntry.also { entry = it } != null) {
                val e = entry ?: continue
                val outFile = File(outputDir, e.name)

                if (e.isDirectory) {
                    outFile.mkdirs()
                    continue
                }

                outFile.parentFile?.mkdirs()

                FileOutputStream(outFile).use { fos ->
                    var read: Int
                    while (zis.read(buffer).also { read = it } > 0) {
                        fos.write(buffer, 0, read)

                        extractedBytes += read
                        val percent = ((extractedBytes * 100) / totalBytes).toInt()

                        if (percent != lastPercent) {
                            lastPercent = percent
                            onProgress(
                                percent.coerceIn(0, 100),
                                extractedFiles,
                                totalFiles
                            )
                        }
                    }
                }
                extractedFiles++
            }
        }

        onProgress(100, totalFiles, totalFiles)
    }

    fun dispose() {
        scope.cancel()
        languageMutexes.clear()
    }
}
