package com.g3.launcher.ui.pane.option.language

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.g3.launcher.manager.*
import com.g3.launcher.model.G3Language
import com.g3.launcher.model.LauncherConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

data class AvailableVoiceLanguage(
    val key: String,
    val title: String,
    val started: Boolean,
    val download: Boolean,
    val progress: Int
)

class LanguageOptionViewModel {
    private val config: LauncherConfig = LauncherManager.config

    private val _voiceLanguages = mutableStateListOf<AvailableVoiceLanguage>()
    val voiceLanguages: List<AvailableVoiceLanguage> get() = _voiceLanguages

    init {
        initLanguagesFast()
    }

    var currentTextLang: G3Language by mutableStateOf(GameSaveManager.getTextLang())
        private set

    var currentVoiceLang: G3Language by mutableStateOf(GameSaveManager.getVoiceLang())
        private set

    var showSubs: Boolean by mutableStateOf(GameSaveManager.isShowSubs())
        private set

    var optionRuIntro: Boolean = config.language == G3Language.Ru
        get() = currentVoiceLang == G3Language.Ru
        private set

    var showRuInto: Boolean by mutableStateOf(GameManager.isUseRuIntro())
        private set

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun initLanguagesFast() {
        val voices = listOf("en", "de", "fr", "pl", "ru")

        voices.forEach { key ->
            val lang = G3Language.fromKey(key)
            val downloaded = config.packages.contains(key)

            _voiceLanguages.add(
                AvailableVoiceLanguage(
                    key = key,
                    title = lang.title,
                    started = false,
                    download = false,
                    progress = if (downloaded) 100 else 0
                )
            )
        }
    }

    fun updateSettingsType(mods: Boolean) {
        GameManager.setGameMode(mods)
        GameSaveManager.setGameMode(mods)

        currentTextLang = GameSaveManager.getTextLang()
        currentVoiceLang = GameSaveManager.getVoiceLang()
        showSubs = GameSaveManager.isShowSubs()
        showRuInto =  GameManager.isUseRuIntro()
    }

    fun downloadLang(key: String, download: Boolean) {
        if (download) {
            startDownload(key)
        } else {
            stopDownload(key)
        }
    }

    private fun startDownload(key: String) {
        updateLanguageState(key, started = true, download = true)

        PackagesManager.downloadLocalization(key) { progress ->
            updateLanguageState(key, started = true, download = true, progress = progress)

            if (progress == 100) {
                completeDownload(key)
            }
        }
    }

    private fun stopDownload(key: String) {
        updateLanguageState(key, started = false, download = false)
        PackagesManager.stopDownload(key)
    }

    private fun completeDownload(key: String) {
        println("completeDownload: $key")

        installLocalization(key)

        // Загрузка завершена
        updateLanguageState(
            key,
            started = false,
            download = false,
            progress = 100
        )

        // Обновляем конфигурацию лаунчера
        scope.launch {
            val packages = config.packages.toMutableList()
            if (!packages.contains(key)) {
                packages.add(key)

                LauncherManager.updateConfig {
                    copy(packages = packages.toList())
                }
                println("config update: $config")

                // Проверяем, не нужно ли обновить текущий язык озвучки
                checkAndUpdateCurrentVoiceLang(key)
            }
        }
    }

    private fun checkAndUpdateCurrentVoiceLang(downloadedKey: String) {
        val downloadedLang = G3Language.fromKey(downloadedKey)
        if (currentVoiceLang == downloadedLang) {
            println("Activating voice language: ${downloadedLang.title}")
            GameManager.setVoiceLanguage(downloadedLang)
        }
    }

    private fun updateLanguageState(
        key: String,
        started: Boolean? = null,
        download: Boolean? = null,
        progress: Int? = null
    ) {
        val index = _voiceLanguages.indexOfFirst { it.key == key }
        if (index != -1) {
            val current = _voiceLanguages[index]
            val updated = current.copy(
                started = started ?: current.started,
                download = download ?: current.download,
                progress = progress ?: current.progress
            )
            _voiceLanguages[index] = updated
            println(
                "Updated language state: key=$key, started=${updated.started}, " +
                        "download=${updated.download}, progress=${updated.progress}"
            )
        }
    }

    fun selectTextLang(language: G3Language) {
        currentTextLang = language
        GameSaveManager.setTextLanguage(language)
    }

    fun selectVoiceLang(language: G3Language) {
        currentVoiceLang = language
        optionRuIntro = language == G3Language.Ru

        GameManager.setVoiceLanguage(language)
        GameSaveManager.setVoiceLanguage(language)

        val selectRuIntro = language == G3Language.Ru
        selectRuIntro(selectRuIntro)
    }

    fun selectSubs(enabled: Boolean) {
        showSubs = enabled
        GameSaveManager.setSubs(enabled)
    }

    fun selectRuIntro(enabled: Boolean) {
        showRuInto = enabled
        GameManager.useRuIntro(enabled)
    }

    private fun installLocalization(key: String) {
        val gamePath = LauncherManager.config.gameDirPath ?: return
        val gameModePath = "${LauncherManager.config.gameDirPath}\\GameWithMods"
        val localeFile = File("app/resources/$key.zip")

        scope.launch {
            delay(1500)
            try {
                localeFile.inputStream().use { input ->
                    extractZipArchive(input, File("$gamePath/Data")) {}
                }

                val modsDir = File(gameModePath)
                if (modsDir.exists()) {
                    localeFile.inputStream().use { input ->
                        extractZipArchive(input, File("$modsDir/Data")) {}
                    }
                }

                println("Installation $key complete")
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    private fun extractZipArchive(inputStream: InputStream, outputDir: File, onProgress: (Int) -> Unit) {
        val buffer = ByteArray(1024)
        ZipInputStream(inputStream).use { zis ->
            var entry: ZipEntry?
            var processedEntries = 0

            while (zis.nextEntry.also { entry = it } != null) {
                val currentEntry = entry ?: continue
                val outputFile = File(outputDir, currentEntry.name)

                if (currentEntry.isDirectory) {
                    outputFile.mkdirs()
                } else {
                    outputFile.parentFile?.mkdirs()
                    FileOutputStream(outputFile).use { fos ->
                        var bytesRead: Int
                        while (zis.read(buffer).also { bytesRead = it } >= 0) {
                            fos.write(buffer, 0, bytesRead)
                        }
                    }
                }

                processedEntries++
                onProgress(processedEntries)
            }
        }

        onProgress(100)
    }
}
