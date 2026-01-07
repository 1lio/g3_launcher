package com.g3.launcher.ui.pane.launcher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.g3.launcher.manager.LauncherManager
import com.g3.launcher.manager.SteamManager
import com.g3.launcher.manager.WindowManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JOptionPane

class MainViewModel {
    var gameStarted: Boolean by mutableStateOf(false)
        private set

    private var checkJob: Job? = null
    private var gameRun: Boolean = false
    private var gameProcess: Process? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private val SAVE_FILE_EXTENSIONS = setOf("g3savcp", "g3savcpx", "g3savcpdat", "g3savcpxdat")
        private val IMPORTANT_FILES = setOf("Shader.Cache", "UserOptions.ini")
        private const val VANILLA_SAVES_DIR = "Vanilla"
        private const val MODS_SAVES_DIR = "WithMods"
    }

    init {
        startGameMonitoring()
    }

    fun playGame() {
        WindowManager.optionsWindow = null
        gameStarted = true
        gameRun = true

        try {
            // 1. Восстанавливаем сохранения ванильной игры
            restoreVanillaSaves()

            // 2. Запускаем обычную игру
            SteamManager.startGame()

            // 3. Запускаем мониторинг для сохранения сейвов после игры
            startSaveSyncThread(false)

        } catch (ex: Exception) {
            showError("Не удалось запустить игру:\n${ex.message}")
            gameRun = false
        }
    }

    fun playWithMods() {
        WindowManager.optionsWindow = null
        gameStarted = true
        gameRun = true

        try {
            val config = LauncherManager.config
            val gameDir = config.gameDirPath

            // 1. Проверяем существует ли папка с модами
            val modsDir = File(gameDir ?: "", "GameWithMods")
            if (!modsDir.exists() || !modsDir.isDirectory) {
                showError("Директория с модами не найдена. Сначала включите моды в настройках.")
                gameRun = false
                return
            }

            // 2. Проверяем существует ли exe файл
            val exeFile = File(modsDir, "Gothic3.exe")
            if (!exeFile.exists()) {
                showError("Файл Gothic3.exe не найден в директории модов.")
                gameRun = false
                return
            }

            // 3. Очищаем текущие файлы сохранений
            clearCurrentSaves()

            // 4. Восстанавливаем сохранения для модов
            restoreModsSaves()

            // 5. Запускаем игру с модами
            val processBuilder = ProcessBuilder(exeFile.absolutePath)
            processBuilder.directory(modsDir)

            gameProcess = processBuilder.start()

            // 6. Запускаем мониторинг для сохранения сейвов после игры
            startSaveSyncThread(true)

        } catch (ex: Exception) {
            showError("Не удалось запустить игру с модами:\n${ex.message}")
            gameRun = false
        }
    }

    private fun restoreVanillaSaves() {
        val gameSavePath = LauncherManager.config.gameSaveDirPath ?: return
        val rootSaveDir = File(gameSavePath)
        val vanillaSavesDir = File(gameSavePath, VANILLA_SAVES_DIR)

        if (!rootSaveDir.exists()) {
            rootSaveDir.mkdirs()
        }

        // Если есть сохранения ванильной игры - восстанавливаем их
        if (vanillaSavesDir.exists() && vanillaSavesDir.isDirectory) {
            copySaveFiles(vanillaSavesDir, rootSaveDir, skipDirectories = listOf(VANILLA_SAVES_DIR, MODS_SAVES_DIR))
        }
    }

    private fun restoreModsSaves() {
        val gameSavePath = LauncherManager.config.gameSaveDirPath ?: return
        val rootSaveDir = File(gameSavePath)
        val modsSavesDir = File(gameSavePath, MODS_SAVES_DIR)

        if (!rootSaveDir.exists()) {
            rootSaveDir.mkdirs()
        }

        // Если есть сохранения с модами - восстанавливаем их
        if (modsSavesDir.exists() && modsSavesDir.isDirectory) {
            copySaveFiles(modsSavesDir, rootSaveDir, skipDirectories = listOf(VANILLA_SAVES_DIR, MODS_SAVES_DIR))
        }
    }

    private fun saveVanillaSaves() {
        val gameSavePath = LauncherManager.config.gameSaveDirPath ?: return
        val rootSaveDir = File(gameSavePath)
        val vanillaSavesDir = File(gameSavePath, VANILLA_SAVES_DIR)

        if (!rootSaveDir.exists()) return

        // Сохраняем текущие сейвы в папку ванильной игры
        vanillaSavesDir.mkdirs()
        copySaveFiles(rootSaveDir, vanillaSavesDir, skipDirectories = listOf(VANILLA_SAVES_DIR, MODS_SAVES_DIR))
    }

    private fun saveModsSaves() {
        val gameSavePath = LauncherManager.config.gameSaveDirPath ?: return
        val rootSaveDir = File(gameSavePath)
        val modsSavesDir = File(gameSavePath, MODS_SAVES_DIR)

        if (!rootSaveDir.exists()) return

        // Сохраняем текущие сейвы в папку модов
        modsSavesDir.mkdirs()
        copySaveFiles(rootSaveDir, modsSavesDir, skipDirectories = listOf(VANILLA_SAVES_DIR, MODS_SAVES_DIR))
    }

    private fun clearCurrentSaves() {
        val gameSavePath = LauncherManager.config.gameSaveDirPath ?: return
        val saveDir = File(gameSavePath)

        if (!saveDir.exists() || !saveDir.isDirectory) return

        // Удаляем только файлы сохранений и важные файлы, но не папки Vanilla и WithMods
        saveDir.listFiles()?.forEach { file ->
            if (shouldDeleteFile(file) && file.name !in listOf(VANILLA_SAVES_DIR, MODS_SAVES_DIR)) {
                try {
                    file.delete()
                } catch (_: Exception) {
                    // Игнорируем ошибки удаления
                }
            }
        }
    }

    private fun startSaveSyncThread(isModded: Boolean) {
        Thread {
            // Даем время игре запуститься
            Thread.sleep(5000)

            var lastGameRunning = true

            // Мониторим процесс игры каждые 2 секунды
            while (lastGameRunning) {
                Thread.sleep(2000)
                lastGameRunning = isGameRunning(isModded)
            }

            // Игра завершилась - сохраняем сейвы
            if (isModded) {
                saveModsSaves()
            } else {
                saveVanillaSaves()
            }
        }.start()
    }

    private fun isGameRunning(isModded: Boolean): Boolean {
        return if (isModded) {
            gameProcess?.isAlive == true
        } else {
            SteamManager.isGameStarted() || SteamManager.isGameProcessRunning()
        }
    }

    // === Вспомогательные методы ===

    private fun copySaveFiles(sourceDir: File, targetDir: File, skipDirectories: List<String> = emptyList()) {
        if (!sourceDir.exists() || !sourceDir.isDirectory) return

        // Копируем только файлы, исключая указанные папки
        sourceDir.listFiles()?.forEach { file ->
            if (file.name !in skipDirectories) {
                val targetFile = File(targetDir, file.name)
                try {
                    if (file.isFile && shouldCopyFile(file)) { // Копируем только файлы
                        file.copyTo(targetFile, overwrite = true)
                    }
                } catch (_: Exception) {
                    // Игнорируем ошибки копирования
                }
            }
        }
    }

    private fun shouldCopyFile(file: File): Boolean {
        if (!file.isFile) return false // Не копируем папки

        val name = file.name
        val extension = file.extension.lowercase()

        // Проверяем расширения .g3savcp, .g3savcpx, .g3savcpdat, .g3savcpxdat
        val isSaveFile = extension in SAVE_FILE_EXTENSIONS

        // Проверяем важные файлы
        val isImportantFile = name in IMPORTANT_FILES

        return isSaveFile || isImportantFile
    }

    private fun shouldDeleteFile(file: File): Boolean {
        if (!file.isFile) return false // Удаляем только файлы

        val name = file.name
        val extension = file.extension.lowercase()

        val isSaveFile = extension in SAVE_FILE_EXTENSIONS
        val isImportantFile = name in IMPORTANT_FILES

        return isSaveFile || isImportantFile
    }

    private fun showError(message: String) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Ошибка",
            JOptionPane.ERROR_MESSAGE
        )
    }

    private fun startGameMonitoring() {
        checkJob?.cancel()

        checkJob = flow {
            while (true) {
                emit(Unit)
                delay(2000)
            }
        }
            .onStart { emit(Unit) }
            .onEach {
                if (gameRun) {
                    delay(5000) // Ждем пока стартанет процесс
                    gameRun = false
                }

                val isRunning = SteamManager.isGameStarted() || SteamManager.isGameProcessRunning() || (gameProcess?.isAlive == true)

                withContext(scope.coroutineContext) {
                    gameStarted = isRunning
                }
            }
            .launchIn(scope)
    }
}
