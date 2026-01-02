package com.g3.launcher.ui.pane.install

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import com.g3.launcher.g3_laucher.generated.resources.Res
import com.g3.launcher.g3_laucher.generated.resources.bg1
import com.g3.launcher.g3_laucher.generated.resources.bg2
import com.g3.launcher.g3_laucher.generated.resources.bg3
import com.g3.launcher.g3_laucher.generated.resources.bg4
import com.g3.launcher.model.LocalConfig
import com.g3.launcher.model.LocalLanguage
import com.g3.launcher.ui.component.LanguageButton
import com.g3.launcher.ui.component.LinearProgress
import com.g3.launcher.ui.component.MainBox
import com.g3.launcher.ui.component.UpdateButton
import com.g3.launcher.ui.pane.install.InstallViewModel.Stage

@Composable
fun ApplicationScope.InstallPane(
    viewModel: InstallViewModel = remember { InstallViewModel() },
    onClose: () -> Unit = ::exitApplication,
) {
    val config = LocalConfig.current
    val strings = LocalLanguage.current.strings

    val stage = viewModel.stage
    val baseDownloadProgress = viewModel.baseProgress

    var closeEnabled by remember { mutableStateOf(true) }

    if (stage is Stage.Setup) {
        closeEnabled = stage.closeEnabled
    }

    MainBox(
        backgroundRes = when (stage) {
            Stage.Welcome -> Res.drawable.bg1
            Stage.PackSelect -> Res.drawable.bg1
            is Stage.SelectDirs -> Res.drawable.bg2
            is Stage.Setup -> Res.drawable.bg3
            is Stage.Error -> Res.drawable.bg4
        },
        closeEnabled = closeEnabled,
        onClose = onClose,
    ) {
        when (stage) {
            Stage.Welcome -> WelcomePane(
                onClickSetup = viewModel::runInstall
            )

            else -> {}
            /*Stage.PackSelect -> PackageSelectorPane(
                onClick = viewModel::install
            )

            is Stage.SelectDirs -> DirectorySelectPane(
                gameDir = stage.gameDir,
                gameDirError = stage.gameDirError,
                gameSavesDir = stage.saveDir,
                gameSaveError = stage.saveDirErrorPath,
                onClickGameDir = viewModel::selectGameDir,
                onClickSavesDir = viewModel::selectGameSaveDir,
                onClickNext = viewModel::continueInstall,
            )

            is Stage.Setup -> InstallationPane(
                progress = viewModel.progress,
                download = viewModel.download,
                text = when (stage.step) {
                    1 -> strings.removingOutdatedFiles
                    2 -> strings.backupSaves
                    3 -> "Downloads: "
                    4 -> "Installing: ${strings.file}: ${stage.num} / ${stage.count}"
                    else -> ""
                }
            )

            is Stage.Error -> FailurePane(
                errorMessage = stage.message
            )*/
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
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 24.dp)
        )

        if (baseDownloadProgress in 1..99) {
            LinearProgress(baseDownloadProgress)
        }
    }
}