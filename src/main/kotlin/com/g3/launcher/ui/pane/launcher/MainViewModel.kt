package com.g3.launcher.ui.pane.launcher

import com.g3.launcher.manager.SteamManager
import com.g3.launcher.manager.WindowManager

class MainViewModel {

    fun playGame() {
        WindowManager.optionsWindow = null

        SteamManager.startGame()
    }

    fun checkGameState(isActiveLauncher: Boolean) {

    }
}
