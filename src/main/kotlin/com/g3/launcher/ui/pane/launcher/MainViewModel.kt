package com.g3.launcher.ui.pane.launcher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.g3.launcher.manager.LauncherManager
import com.g3.launcher.manager.RegistryManager
import com.g3.launcher.manager.SteamManager
import com.g3.launcher.manager.WindowManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.io.File

class MainViewModel {
    var gameStarted: Boolean by mutableStateOf(false)
        private set

    private var checkJob: Job? = null
    private var gameRun: Boolean = false
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        startGameMonitoring()
    }

    fun playGame() {
        WindowManager.optionsWindow = null

        gameStarted = true
        gameRun = true

        SteamManager.startGame()
    }

    fun playWithMods() {
        WindowManager.optionsWindow = null

        gameStarted = true
        gameRun = true

        try {
            // Пока просто запус из другой директории
            // Сохранения пока общие
            val exe = LauncherManager.config.gameDirPath?.let { "$it\\GameWithMods\\Gothic3.exe" } ?: return
            val exeFile = File(exe)
            val processBuilder = ProcessBuilder(exe)

            // Устанавливаем рабочую директорию (где находится exe файл)
            processBuilder.directory(exeFile.parentFile)

            val process = processBuilder.start()
            Thread {
                process.waitFor()
                gameRun = false
            }.start()

        } catch (ex: Exception) {
            gameRun = false
        }
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
                    delay(5000) // Джем пока стартанет процесс
                    gameRun = false
                }

                val isRunning = SteamManager.isGameStarted() || SteamManager.isGameProcessRunning()

                withContext(scope.coroutineContext) {
                    gameStarted = isRunning
                }
            }
            .launchIn(scope)
    }

    fun dispose() {
        checkJob?.cancel()
        scope.cancel()
    }
}
