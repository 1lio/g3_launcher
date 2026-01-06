package com.g3.launcher.ui.pane.launcher

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
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
import com.g3.launcher.g3_laucher.generated.resources.headline_line
import com.g3.launcher.manager.WindowManager
import com.g3.launcher.model.LocalConfig
import com.g3.launcher.model.LocalLanguage
import com.g3.launcher.ui.component.G3Text
import com.g3.launcher.ui.component.LanguageButton
import com.g3.launcher.ui.component.MainBox
import com.g3.launcher.ui.component.UpdateButton
import com.g3.launcher.ui.theme.ColorText
import com.g3.launcher.ui.theme.ColorTextSecondary
import com.g3.launcher.ui.window.OptionWindows
import org.jetbrains.compose.resources.painterResource

@Composable
fun ApplicationScope.MainPane(
    viewModel: MainViewModel = remember { MainViewModel() },
    onClose: () -> Unit = ::exitApplication,
) {
    val strings = LocalLanguage.current.strings
    val config = LocalConfig.current
    var showOption by remember { mutableStateOf(false) }

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
                G3Text(
                    text = strings.play,
                    textAlign = TextAlign.Center,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    hoverable = !viewModel.gameStarted,
                    color = if (viewModel.gameStarted) ColorTextSecondary else ColorText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(null, null, enabled = !viewModel.gameStarted) {
                            viewModel.playGame()
                        }
                )

                if (config.mods) {
                    G3Text(
                        text = strings.playMods,
                        hoverable = !viewModel.gameStarted,
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp,
                        color = if (viewModel.gameStarted) ColorTextSecondary else ColorText,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(null, null, enabled = !viewModel.gameStarted) {
                                viewModel.playWithMods()
                            }
                    )
                }

                Image(
                    painter = painterResource(Res.drawable.headline_line),
                    contentDescription = null,
                    modifier = Modifier
                        .height(14.dp)
                        .fillMaxWidth()
                )

                G3Text(
                    text = strings.options,
                    textAlign = TextAlign.Center,
                    color = if (viewModel.gameStarted) ColorTextSecondary else ColorText,
                    hoverable = !viewModel.gameStarted,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(null, null) {
                            WindowManager.showOptions {
                                showOption = true
                            }
                        }

                )
            }

            Divider(modifier = Modifier.height(24.dp))
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

        if (showOption) {
            OptionWindows {
                showOption = false
                WindowManager.bringMainWindow()
            }
        }
    }
}
