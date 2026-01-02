package com.g3.launcher.ui.pane.launcher

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ApplicationScope
import com.g3.launcher.g3_laucher.generated.resources.Res
import com.g3.launcher.g3_laucher.generated.resources.bg5
import com.g3.launcher.manager.SteamManager
import com.g3.launcher.manager.WindowManager
import com.g3.launcher.model.LocalConfig
import com.g3.launcher.model.LocalLanguage
import com.g3.launcher.ui.component.G3Text
import com.g3.launcher.ui.component.LanguageButton
import com.g3.launcher.ui.component.MainBox
import com.g3.launcher.ui.component.UpdateButton
import com.g3.launcher.ui.theme.ColorTextGray
import com.g3.launcher.ui.window.onActiveWindow
import kotlinx.coroutines.delay

@Composable
fun ApplicationScope.MainPane(
    viewModel: MainViewModel = remember { MainViewModel() },
    onClose: () -> Unit = ::exitApplication,
) {
    LaunchedEffect(Unit) {
        WindowManager.mainWindow?.onActiveWindow {
            viewModel.checkGameState(isActiveLauncher = it)
        }
    }

    var isActiveWindow by remember { mutableStateOf(true) }
    val strings = LocalLanguage.current.strings
    val config = LocalConfig.current

    var isStarted by remember { mutableStateOf(false) }
    var start by remember { mutableStateOf(false) }

    LaunchedEffect(isActiveWindow && start) {
        if (start) {
            isStarted = true

            WindowManager.optionsWindow = null

            delay(5000)

            if (isActiveWindow) {
                isStarted = SteamManager.isGameProcessRunning()

                if (!isStarted) {
                    start = false
                }
            }
        }
    }

    MainBox(
        backgroundRes = Res.drawable.bg5,
        onClose = onClose,
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(alignment = Alignment.CenterEnd)
                .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(320.dp)
                    .wrapContentHeight()
            ) {
                if (!isStarted) {
                    G3Text(
                        text = strings.play,
                        textAlign = TextAlign.Center,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        hoverable = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.playGame()
                                SteamManager.startGame()
                                start = true
                            }
                    )
                } else {
                    G3Text(
                        text = strings.play,
                        color = ColorTextGray,
                        textAlign = TextAlign.Center,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        hoverable = false,
                        shadow = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (config.availableUpdate) {
            UpdateButton(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 72.dp)
            )
        }

        LanguageButton(
            modifier = Modifier
                .align(alignment = Alignment.BottomStart)
                .padding(24.dp)
        )
    }
}
