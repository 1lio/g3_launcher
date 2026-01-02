package com.g3.launcher.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.window.ApplicationScope
import com.g3.launcher.Constants
import com.g3.launcher.manager.WindowManager
import com.g3.launcher.model.LocalConfig
import com.g3.launcher.ui.pane.launcher.MainPane
import com.g3.launcher.ui.pane.install.InstallPane

@Composable
fun ApplicationScope.LauncherWindow() {
    val isInstalled = LocalConfig.current.installed

    G3Window(
        width = Constants.LAUNCHER_WIDTH,
        height = Constants.LAUNCHER_HEIGHT,
        title = Constants.LAUNCHER_WINDOW_TITLE,
    ) {
        if (isInstalled) {
            MainPane()
        } else {
            InstallPane()
        }

        DisposableEffect(Unit) {
            WindowManager.mainWindow = window
            onDispose {
                WindowManager.mainWindow = null
            }
        }
    }
}
