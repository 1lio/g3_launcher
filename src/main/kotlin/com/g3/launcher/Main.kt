package com.g3.launcher

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.application
import com.g3.launcher.manager.LauncherManager
import com.g3.launcher.model.LocalConfig
import com.g3.launcher.model.LocalLanguage
import com.g3.launcher.ui.theme.LauncherTheme
import com.g3.launcher.ui.window.LauncherWindow

fun main() = application {
    val config by LauncherManager.configState

    LaunchedEffect(Unit) {
        LauncherManager.checkForUpdates()
    }

    LauncherTheme {
        CompositionLocalProvider(
            values = arrayOf(
                LocalConfig provides config,
                LocalLanguage provides config.language,
            )
        ) {
            LauncherWindow()
        }
    }
}
