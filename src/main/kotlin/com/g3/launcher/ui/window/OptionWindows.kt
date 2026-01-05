package com.g3.launcher.ui.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.g3.launcher.manager.WindowManager
import com.g3.launcher.ui.component.BgPosition
import com.g3.launcher.ui.component.MainBox
import com.g3.launcher.ui.component.option.OptionTabSelector

@Composable
fun ApplicationScope.OptionWindows(
    onClose: () -> Unit,
) {
    var tabIndex by remember { mutableStateOf(0) }

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

                Box(
                    Modifier
                        .padding(top = 36.dp)
                        .fillMaxSize()
                ) {
                    when (tabIndex) {
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
