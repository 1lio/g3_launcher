package com.g3.launcher.ui.pane.install

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.g3.launcher.manager.LauncherManager
import com.g3.launcher.manager.PackagesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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
            val step: Int,
            val num: Int = 1,
            val count: Int = 64,
            val closeEnabled: Boolean = true
        ) : Stage

        class Error(
            val message: String
        ) : Stage
    }

    var stage: Stage by mutableStateOf(Stage.Welcome)
        private set

    var baseProgress: Int by mutableIntStateOf(0)
        private set

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun runInstall() {
        stage = Stage.PackSelect

        scope.launch {
            PackagesManager.installBasePackage {
                println("$it")
                baseProgress = it
            }
        }
    }

}
