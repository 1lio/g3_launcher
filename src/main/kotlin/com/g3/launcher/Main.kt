package com.g3.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import com.g3.launcher.manager.LauncherManager
import com.g3.launcher.model.G3Language
import com.g3.launcher.model.LocalConfig
import com.g3.launcher.model.LocalLanguage
import com.g3.launcher.ui.theme.LauncherTheme
import com.g3.launcher.ui.window.G3Window

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
            G3Window(800, 600, "test") {
                val lang = LocalLanguage.current
                val str = lang.strings

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = config.toString()
                    )

                    Text(
                        text = str.nextAction
                    )

                    Text("test", modifier = Modifier.clickable {
                        if (lang == G3Language.Ru) {
                            LauncherManager.updateConfig {
                                copy(language = G3Language.En)
                            }
                        } else {
                            LauncherManager.updateConfig {
                                copy(language = G3Language.Ru)
                            }
                        }
                    })
                }
            }
        }
    }
}
