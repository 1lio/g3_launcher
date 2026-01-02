package com.g3.launcher.ui.pane.install

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.g3.launcher.manager.LauncherManager
import com.g3.launcher.manager.PackagesManager
import com.g3.launcher.mapper.toLocales
import com.g3.launcher.model.InstallPackages
import com.g3.launcher.model.LauncherConfig
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.swing.JFileChooser

class InstallViewModel {
    sealed interface Stage {
        object Welcome : Stage
        object PackSelect : Stage
        class SelectDirs(
            val gameDir: String? = LauncherManager.config.gameDirPath,
            val gameDirError: String? = null,
            val saveDir: String? = LauncherManager.config.gameSaveDirPath,
            val saveDirErrorPath: String? = null,
        ) : Stage

        class Setup(
            val closeEnabled: Boolean = true,
            val steps: List<SetupStep> = emptyList()
        ) : Stage

        class Error(val message: String) : Stage
    }

    sealed interface SetupStep {
        class BaseDownload(val progress: Int) : SetupStep
        class BaseInstall(val progress: Int) : SetupStep
        class LocalizationDownload(val progress: Int, val count: Int, val total: Int) : SetupStep
        class LocalizationInstall(val progress: Int, val count: Int, val total: Int) : SetupStep
        class CleanGameDir(val complete: Boolean) : SetupStep
        class CreateBackup(val complete: Boolean) : SetupStep
    }

    // Состояния загрузки
    private var selectedLocales = mutableSetOf<String>()
    private val downloadProgressMap = mutableMapOf<String, Int>()
    private val downloadCompleteMap = mutableMapOf<String, Boolean>()
    private var baseDownloadComplete = false

    // Состояния установки
    private var baseInstallComplete = false
    private val installCompleteMap = mutableMapOf<String, Boolean>()

    // Дополнительные состояния
    private var cleaningComplete = false
    private var backupComplete = false

    var stage: Stage by mutableStateOf(Stage.Welcome)
        private set

    var baseProgress: Int by mutableIntStateOf(0)
        private set

    var downloadProgress: Int by mutableIntStateOf(0)
        private set

    var installProgress: Int by mutableIntStateOf(0)
        private set

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val config: LauncherConfig = LauncherManager.config

    init {
        // Инициализируем загрузку английской локализации по умолчанию
        selectedLocales.add("en")
        downloadProgressMap["en"] = 0
        downloadCompleteMap["en"] = false
    }

    fun runInstall() {
        stage = Stage.PackSelect
        startBaseDownload()
        startDefaultLocalizationDownload()
    }

    private fun startBaseDownload() {
        scope.launch {
            try {
                if (config.packages.contains("base")) {
                    baseDownloadComplete = true
                    baseProgress = 100
                    downloadProgressMap["base"] = 100

                    updateDownloadProgress()
                    updateSetupStageIfActive()

                    if (areAllDownloadsComplete() && stage is Stage.Setup) {
                        checkAndStartInstallation()
                    }
                } else {
                    PackagesManager.installBasePackage { progress ->
                        baseProgress = progress
                        downloadProgressMap["base"] = progress
                        if (progress == 100) {
                            baseDownloadComplete = true
                            LauncherManager.updateConfig {
                                val update = config.packages.toMutableList()
                                update.add("base")

                                copy(packages = update.toSet().toList())
                            }
                        }

                        updateDownloadProgress()
                        updateSetupStageIfActive()

                        // Если база загружена и мы на Setup - можно начинать очистку и установку
                        if (progress == 100 && stage is Stage.Setup) {
                            checkAndStartInstallation()
                        }
                    }
                }
                println("Base download completed")
            } catch (e: Exception) {
                println("Base download failed: ${e.message}")
                stage = Stage.Error("Failed to download base package: ${e.message}")
            }
        }
    }

    private fun startDefaultLocalizationDownload() {
        // Загружаем английскую локализацию по умолчанию
        scope.launch {
            try {
                if (config.packages.contains("en")) {
                    downloadProgressMap["en"] = 100

                    updateDownloadProgress()
                    updateSetupStageIfActive()

                    // Если все загружено и мы на Setup - можно начинать установку локализаций
                    if (areAllDownloadsComplete() && stage is Stage.Setup) {
                        checkAndStartInstallation()
                    }
                } else {
                    PackagesManager.installLanguagePackage("en") { progress ->
                        downloadProgressMap["en"] = progress
                        if (progress == 100) {
                            downloadCompleteMap["en"] = true
                            LauncherManager.updateConfig {
                                val update = config.packages.toMutableList()
                                update.add("en")

                                copy(packages = update.toSet().toList())
                            }
                        }

                        updateDownloadProgress()
                        updateSetupStageIfActive()

                        // Если все загружено и мы на Setup - можно начинать установку локализаций
                        if (areAllDownloadsComplete() && stage is Stage.Setup) {
                            checkAndStartInstallation()
                        }
                    }
                }
                println("Default EN localization download completed")
            } catch (e: Exception) {
                println("EN localization download failed: ${e.message}")
            }
        }
    }

    fun installPackages(installPackage: InstallPackages) {
        stage = Stage.SelectDirs()
        val newSelectedLocales = installPackage.toLocales().toMutableSet()

        // Добавляем английскую локализацию по умолчанию, если не выбрана
        if (newSelectedLocales.isNotEmpty()) {
            selectedLocales = newSelectedLocales
        }
        // Если не выбрано ни одной локализации, оставляем английскую по умолчанию

        // Загружаем новые локализации, которые еще не загружались
        val localesToDownload = selectedLocales.filter { locale ->
            !downloadCompleteMap.containsKey(locale) || downloadCompleteMap[locale] == false
        }

        localesToDownload.forEach { locale ->
            if (locale != "en" || !downloadCompleteMap.containsKey("en")) {
                startLocalizationDownload(locale)
            }
        }
    }

    private fun startLocalizationDownload(locale: String) {
        scope.launch {
            try {
                if (config.packages.contains(locale)) {
                    downloadCompleteMap[locale] = true
                    downloadProgressMap[locale] = 100

                    updateDownloadProgress()
                    updateSetupStageIfActive()

                    if (areAllDownloadsComplete() && stage is Stage.Setup) {
                        checkAndStartInstallation()
                    }
                } else {
                    PackagesManager.installLanguagePackage(locale) { progress ->
                        downloadProgressMap[locale] = progress
                        if (progress == 100) {
                            downloadCompleteMap[locale] = true

                            LauncherManager.updateConfig {
                                val update = config.packages.toMutableList()
                                update.add(locale)

                                copy(packages = update.toSet().toList())
                            }
                        }

                        updateDownloadProgress()
                        updateSetupStageIfActive()

                        // Если все загружено и мы на Setup - можно начинать установку локализаций
                        if (areAllDownloadsComplete() && stage is Stage.Setup) {
                            checkAndStartInstallation()
                        }
                    }
                }
                println("$locale localization download completed")
            } catch (e: Exception) {
                println("$locale localization download failed: ${e.message}")
            }
        }
    }

    fun continueInstall() {
        stage = Stage.Setup(
            steps = buildSetupSteps()
        )

        // Проверяем, можно ли начинать установку
        checkAndStartInstallation()
    }

    private fun checkAndStartInstallation() {
        if (stage !is Stage.Setup) return

        scope.launch {
            try {
                // 3. Если базовый архив загружен - выполняем очистку и установку
                if (baseDownloadComplete && !baseInstallComplete) {
                    performGameCleaning()
                    installBasePackage()
                }

                // 4. После распаковки base можно распаковывать локализации
                if (baseInstallComplete && areAllDownloadsComplete()) {
                    installLocalizationPackages()
                }

                // 5. Последний шаг - бекап
                if (areAllInstallationsComplete()) {
                    createBackup()
                }
            } catch (e: Exception) {
                println("Installation failed: ${e.message}")
                stage = Stage.Error("Installation failed: ${e.message}")
            }
        }
    }

    private fun performGameCleaning() {
        println("Remove old files")

        updateStep(SetupStep.CleanGameDir(false))

        val gameDir: String = LauncherManager.config.gameDirPath ?: return
        val savesDir = LauncherManager.config.gameSaveDirPath ?: return

        val filesToDelete = listOf(
            "CP_Changelog_en.txt",
            "CP_Readme_en.txt",
            "Disclaimer_en.txt",
            "Exporter.dll",
            "MSVCRT.DLL",
            "SHW32.DLL",
            "copublisher.url",
            "fmod.dll",
            "ge3dialogs.dll",
            "lib3ds.dll",
            "libexpat.dll",
            "msdbi.dll",
            "msvcm80.dll",
            "msvcp71.dll",
            "msvcp80.dll",
            "msvcr71.dll",
            "msvcr80.dll",
            "protect.dll",
            "sapi_lipsync.dll",
            "site.url",
            "vcomp.dll",
            "Data${File.separator}_compiledAnimation.p00",
            "Data${File.separator}_compiledImage.p00",
            "Data${File.separator}Infos.p01",
            "Data${File.separator}Infos.p00",
            "Data${File.separator}Library.p00",
            "Data${File.separator}Quests.p00",
            "Data${File.separator}Quests.p01",
            "Data${File.separator}Sound.p01",
            "Data${File.separator}Music.p00",
            "Data${File.separator}Speech_German.p00",
            "Data${File.separator}Templates.p01",
            "Data${File.separator}_compiledImage.p01",
            "Data${File.separator}Projects_compiled.p00",
            "Data${File.separator}Projects_compiled.p01",
            "Data${File.separator}_intern.pak",
            "Data${File.separator}gui.p00",
            "Data${File.separator}gui.p01",
            "Data${File.separator}Sound.p00",
            "Data${File.separator}Strings.p00",
            "Data${File.separator}Templates.p00",
            "Data${File.separator}Video${File.separator}G3_Logo_02.bik",
            "Data${File.separator}Video${File.separator}G3_Logo_03.bik",
            "Data${File.separator}Video${File.separator}G3_Logo_04.bik",
            "Data${File.separator}Video${File.separator}G3_Credits2.bik",
            "Ini${File.separator}G3_World_01_local.wrldatasc",
            "Ini${File.separator}ge3local.ini",
            "Ini${File.separator}keyboard_and_console.txt",
        )

        var deletedCount = 0
        var failedCount = 0

        // Удаляем файлы с логированием
        filesToDelete.forEach { filePath ->
            val file = File(gameDir, filePath)
            try {
                if (file.exists()) {
                    if (file.delete()) {
                        println("Удален: $filePath")
                        deletedCount++
                    } else {
                        println("Не удалось удалить: $filePath")
                        failedCount++
                    }
                } else {
                    println("Файл не найден (пропущен): $filePath")
                }
            } catch (e: Exception) {
                println("Ошибка при удалении $filePath: ${e.message}")
                failedCount++
            }
        }

        // Удаляем папку Materials
        val materialsDir = File(gameDir, "Data${File.separator}Materials")
        if (materialsDir.exists() && materialsDir.isDirectory) {
            try {
                if (deleteDirectory(materialsDir)) {
                    println("Удалена папка: Data/Materials")
                    deletedCount++
                } else {
                    println("Не удалось удалить папку: Data/Materials")
                    failedCount++
                }
            } catch (e: Exception) {
                println("Ошибка при удалении папки Materials: ${e.message}")
                failedCount++
            }
        } else {
            println("Папка Materials не найдена")
        }

        // Удаляем папку Docs
        val docs = File(gameDir, "Data${File.separator}Docs")
        if (docs.exists() && docs.isDirectory) {
            try {
                if (deleteDirectory(docs)) {
                    println("Удалена папка: Data/Docs")
                    deletedCount++
                } else {
                    println("Не удалось удалить папку: Data/Docs")
                    failedCount++
                }
            } catch (e: Exception) {
                println("Ошибка при удалении папки Docs: ${e.message}")
                failedCount++
            }
        } else {
            println("Папка Docs не найдена")
        }

        // Удаляем логи и кэши сохранений
        val saves = File(savesDir)
        if (saves.exists() && saves.isDirectory) {
            saves.listFiles()?.forEach { file ->
                if (file.isFile && (file.name.contains(".log") || file.name.contains(".Cache"))) {
                    try {
                        if (file.delete()) {
                            println("Удален файл сохранений: ${file.name}")
                            deletedCount++
                        } else {
                            println("Не удалось удалить файл сохранений: ${file.name}")
                            failedCount++
                        }
                    } catch (e: Exception) {
                        println("Ошибка при удалении файла ${file.name}: ${e.message}")
                        failedCount++
                    }
                }
            }
        } else {
            println("Папка сохранений не найдена: $savesDir")
        }

        cleaningComplete = true
        updateStep(SetupStep.CleanGameDir(true))
        updateInstallProgress()

        println("Old files removed. Удалено: $deletedCount, Не удалось: $failedCount")
    }

    private fun deleteDirectory(directory: File): Boolean {
        return if (directory.exists()) {
            val contents = directory.listFiles()
            if (contents != null) {
                for (file in contents) {
                    if (file.isDirectory) {
                        deleteDirectory(file)
                    } else {
                        if (!file.delete()) {
                            println("Не удалось удалить файл в папке: ${file.absolutePath}")
                        }
                    }
                }
            }
            directory.delete()
        } else {
            true // Если папка не существует, считаем что удалена
        }
    }

    private fun installBasePackage() {
        val baseFile = File("app/resources/base.zip")
        if (!baseFile.exists()) {
            throw IllegalStateException("Base file not found")
        }

        val gamePath = LauncherManager.config.gameDirPath ?: return

        try {
            baseFile.inputStream().use { input ->
                extractZipArchive(input, File(gamePath)) { progress ->
                    updateStep(SetupStep.BaseInstall(progress))
                    updateInstallProgress()
                }
            }
        } catch (e: Exception) {
            stage = Stage.Error("Install patches: internal error")
            e.printStackTrace()
            throw e
        }

        baseInstallComplete = true
        updateInstallProgress()
        println("Base installation completed")
    }

    private fun installLocalizationPackages() {
        val total = selectedLocales.size
        var installedCount = 0

        // Проверяем, какие локализации уже установлены
        selectedLocales.forEach { locale ->
            if (installCompleteMap[locale] == true) {
                installedCount++
            }
        }

        updateStep(
            SetupStep.LocalizationInstall(
                progress = if (total > 0) (installedCount * 100) / total else 0,
                count = installedCount,
                total = total
            )
        )

        val gamePath = LauncherManager.config.gameDirPath ?: return

        for (locale in selectedLocales) {
            if (installCompleteMap[locale] == true) continue

            val localeFile = File("app/resources/$locale.zip")
            if (!localeFile.exists()) continue

            try {
                localeFile.inputStream().use { input ->
                    extractZipArchive(input, File("$gamePath/Data")) {}
                }
            } catch (e: Exception) {
                stage = Stage.Error("Install locale $locale: internal error")
                e.printStackTrace()
                throw e
            }

            installedCount++
            installCompleteMap[locale] = true

            updateStep(
                SetupStep.LocalizationInstall(
                    progress = if (total > 0) (installedCount * 100) / total else 0,
                    count = installedCount,
                    total = total
                )
            )
            updateInstallProgress()
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

    private suspend fun createBackup() {
        updateStep(SetupStep.CreateBackup(false))

        val savesDir = LauncherManager.config.gameSaveDirPath ?: return

        val backupDir = File(savesDir, "Save_Backup_${System.currentTimeMillis()}")
        backupDir.mkdirs()

        File(savesDir)
            .listFiles()
            ?.toList()
            ?.parallelStream()
            ?.forEach { file ->
                if (file.isFile) {
                    val backupFile = File(backupDir, file.name)
                    Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
            }

        delay(500)

        backupComplete = true
        updateStep(SetupStep.CreateBackup(true))
        updateInstallProgress()

        // Installation completed
        println("Installation completed successfully!")

        LauncherManager.updateConfig { copy(installed = true) }
    }

    private fun updateStep(newStep: SetupStep) {
        if (stage is Stage.Setup) {
            val currentSteps = (stage as Stage.Setup).steps.toMutableList()

            when (newStep) {
                is SetupStep.BaseDownload -> {
                    currentSteps[0] = newStep
                }

                is SetupStep.LocalizationDownload -> {
                    currentSteps[1] = newStep
                }

                is SetupStep.CleanGameDir -> {
                    currentSteps[2] = newStep
                }

                is SetupStep.BaseInstall -> {
                    currentSteps[3] = newStep
                }

                is SetupStep.LocalizationInstall -> {
                    currentSteps[4] = newStep
                }

                is SetupStep.CreateBackup -> {
                    currentSteps[5] = newStep
                }
            }

            stage = Stage.Setup(
                closeEnabled = true,
                steps = currentSteps
            )
        }
    }

    private fun buildSetupSteps(): List<SetupStep> {
        return buildList {
            // База: используем текущий прогресс загрузки
            val baseDownloadProgress = downloadProgressMap["base"] ?: 0
            add(SetupStep.BaseDownload(baseDownloadProgress))

            // Загрузка локализаций
            val downloadedCount = getDownloadedLocalizationCount()
            val totalLocales = selectedLocales.size
            val localizationDownloadProgress = getLocalizationDownloadProgress()
            add(
                SetupStep.LocalizationDownload(
                    progress = localizationDownloadProgress,
                    count = downloadedCount,
                    total = totalLocales
                )
            )
            // Очистка
            add(SetupStep.CleanGameDir(cleaningComplete))

            // Установка базы
            val baseInstallProgress = if (baseInstallComplete) 100 else 0
            add(SetupStep.BaseInstall(baseInstallProgress))

            // Установка локализаций
            val installedCount = installCompleteMap.count { it.value }
            val localizationInstallProgress = if (totalLocales > 0) {
                (installedCount * 100) / totalLocales
            } else {
                0
            }
            add(
                SetupStep.LocalizationInstall(
                    progress = localizationInstallProgress,
                    count = installedCount,
                    total = totalLocales
                )
            )

            add(SetupStep.CreateBackup(backupComplete))
        }
    }

    private fun updateSetupStageIfActive() {
        if (stage is Stage.Setup) {
            stage = Stage.Setup(
                closeEnabled = true,
                steps = buildSetupSteps()
            )
        }
    }

    private fun updateDownloadProgress() {
        // Глобальный прогресс загрузки: база + локализации
        val totalItems = 1 + selectedLocales.size // база + все локализации

        val baseProgressValue = downloadProgressMap["base"] ?: 0
        val localesProgressSum = selectedLocales.sumOf { downloadProgressMap[it] ?: 0 }

        downloadProgress = if (totalItems > 0) {
            (baseProgressValue + localesProgressSum) / totalItems
        } else {
            0
        }
    }

    private fun updateInstallProgress() {
        // Глобальный прогресс установки: 6 шагов (база, локализации, очистка, бекап)
        val totalSteps = 6
        var completedSteps = 0

        // База загружена?
        if ((downloadProgressMap["base"] ?: 0) == 100) completedSteps++

        // База установлена?
        if (baseInstallComplete) completedSteps++

        // Локализации загружены?
        if (getLocalizationDownloadProgress() == 100) completedSteps++

        // Локализации установлены?
        val totalLocales = selectedLocales.size
        val installedCount = installCompleteMap.count { it.value }
        if (totalLocales == 0 || installedCount == totalLocales) completedSteps++

        // Очистка выполнена
        if (cleaningComplete) completedSteps++

        // Бекап выполнен
        if (backupComplete) completedSteps++

        installProgress = (completedSteps * 100) / totalSteps
    }

    private fun getLocalizationDownloadProgress(): Int {
        if (selectedLocales.isEmpty()) return 100 // Нет локализаций - считаем загруженными

        val totalProgress = selectedLocales.sumOf { locale ->
            downloadProgressMap[locale] ?: 0
        }

        return if (selectedLocales.isNotEmpty()) {
            totalProgress / selectedLocales.size
        } else {
            0
        }
    }

    private fun getDownloadedLocalizationCount(): Int {
        return selectedLocales.count { locale ->
            (downloadProgressMap[locale] ?: 0) == 100
        }
    }

    private fun areAllDownloadsComplete(): Boolean {
        // Проверяем базу
        val baseComplete = (downloadProgressMap["base"] ?: 0) == 100

        // Проверяем локализации
        val localesComplete = selectedLocales.all { locale ->
            (downloadProgressMap[locale] ?: 0) == 100
        }

        return baseComplete && localesComplete
    }

    private fun areAllInstallationsComplete(): Boolean {
        return baseInstallComplete &&
                selectedLocales.all { installCompleteMap[it] == true } &&
                cleaningComplete
    }

    // =======
    fun selectGameDir() {
        val containsFile = "Gothic3.exe"
        val currentPath = openDirectoryChooserSwing(
            initialDirectory = config.gameDirPath?.let { File(it) }
        )?.path

        if (currentPath != null) {
            val file = File("$currentPath\\$containsFile")
            if (!file.exists()) {
                stage = Stage.SelectDirs(
                    gameDir = null,
                    gameDirError = containsFile,
                    saveDir = LauncherManager.config.gameSaveDirPath,
                    saveDirErrorPath = null
                )
            } else {
                LauncherManager.updateConfig { copy(gameDirPath = currentPath) }

                stage = Stage.SelectDirs(
                    gameDir = LauncherManager.config.gameDirPath,
                    gameDirError = null,
                    saveDir = LauncherManager.config.gameSaveDirPath,
                    saveDirErrorPath = null
                )
            }
        }
    }

    fun selectGameSaveDir() {
        val containsFile = "UserOptions.ini"
        val currentPath = openDirectoryChooserSwing(
            initialDirectory = config.gameSaveDirPath?.let { File(it) }
        )?.path

        if (currentPath != null) {
            val file = File("$currentPath\\$containsFile")
            if (!file.exists()) {
                stage = Stage.SelectDirs(
                    gameDir = LauncherManager.config.gameDirPath,
                    gameDirError = null,
                    saveDir = null,
                    saveDirErrorPath = containsFile
                )
            } else {
                LauncherManager.updateConfig { copy(gameSaveDirPath = currentPath) }

                stage = Stage.SelectDirs(
                    gameDir = LauncherManager.config.gameDirPath,
                    gameDirError = null,
                    saveDir = LauncherManager.config.gameSaveDirPath,
                    saveDirErrorPath = null
                )
            }
        }
    }

    private fun openDirectoryChooserSwing(
        initialDirectory: File? = null
    ): File? {
        val fileChooser = JFileChooser()
        fileChooser.dialogTitle = LauncherManager.config.language.strings.selectDirectory
        fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        initialDirectory?.let { dir ->
            if (dir.exists()) {
                fileChooser.currentDirectory = dir
            }
        }

        val result = fileChooser.showOpenDialog(null)
        return when (result) {
            JFileChooser.APPROVE_OPTION -> {
                fileChooser.selectedFile
            }

            JFileChooser.CANCEL_OPTION -> {
                return initialDirectory
            }

            else -> {
                null
            }
        }
    }
}

