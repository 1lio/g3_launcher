package com.g3.launcher.ui.pane.launcher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

        SteamManager.startGame()  // Какая-то логика на запуск игры с модами, скорее из другой директории, пока просто запуск
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
