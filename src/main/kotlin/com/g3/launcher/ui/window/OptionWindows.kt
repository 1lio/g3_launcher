package com.g3.launcher.ui.window

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import com.g3.launcher.Constants
import com.g3.launcher.g3_laucher.generated.resources.Res
import com.g3.launcher.g3_laucher.generated.resources.bg_options
import com.g3.launcher.g3_laucher.generated.resources.headline_line
import com.g3.launcher.manager.LauncherManager
import com.g3.launcher.manager.WindowManager
import com.g3.launcher.model.LocalConfig
import com.g3.launcher.ui.component.BgPosition
import com.g3.launcher.ui.component.MainBox
import com.g3.launcher.ui.component.option.OptionCheckBox
import com.g3.launcher.ui.component.option.OptionTabSelector
import com.g3.launcher.ui.pane.option.language.OptionLanguagePane
import com.g3.launcher.ui.pane.option.other.OtherOptionPane
import org.jetbrains.compose.resources.painterResource

@Composable
fun ApplicationScope.OptionWindows(
    onClose: () -> Unit,
) {
    var tabIndex by remember { mutableStateOf(0) }
    val config = LocalConfig.current

    LaunchedEffect(config) {
        println(config.modsConfig)
    }

    G3Window(
        width = Constants.OPTIONS_WIDTH,
        height = Constants.OPTIONS_HEIGHT,
        title = Constants.OPTIONS_WINDOW_TITLE,
    ) {
        MainBox(
            backgroundRes = Res.drawable.bg_options,
            backgroundPosition = BgPosition.Bottom,
            onClose = onClose,
        ) {
            Column(
                modifier = Modifier.align(alignment = Alignment.TopCenter)
                    .padding(horizontal = 36.dp, vertical = 64.dp),
            ) {
                OptionTabSelector {
                    tabIndex = it
                }

                if (config.mods && tabIndex in 0..2) {
                    Column(
                        modifier = Modifier.padding(top = 36.dp),
                        verticalArrangement = Arrangement.spacedBy(36.dp)
                    ) {
                        OptionCheckBox(
                            text = "Конфигурация для игры с модами",
                            description = "Текущие настройки конфигурации",
                            checked = config.modsConfig,
                            onCheckedChange = {
                                LauncherManager.updateConfig { copy(modsConfig = it) }
                            }
                        )

                        Image(
                            painter = painterResource(Res.drawable.headline_line),
                            contentDescription = null,
                            modifier = Modifier
                                // .height(12.dp)
                                .fillMaxWidth()
                        )
                    }
                }

                Box(
                    Modifier
                        .padding(top = 36.dp)
                        .fillMaxSize()
                ) {
                    when (tabIndex) {
                        0 -> OptionLanguagePane()
                        1 -> {}
                        2 -> {}
                        3 -> OtherOptionPane()
                        else -> {}
                    }
                }
            }
        }

        DisposableEffect(Unit) {
            WindowManager.optionsWindow = window
            onDispose {
                WindowManager.optionsWindow = null
            }
        }
    }
}
