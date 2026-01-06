package com.g3.launcher.ui.pane.option.other

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g3.launcher.g3_laucher.generated.resources.Res
import com.g3.launcher.g3_laucher.generated.resources.ic_file
import com.g3.launcher.g3_laucher.generated.resources.ic_folder
import com.g3.launcher.model.LocalConfig
import com.g3.launcher.model.LocalLanguage
import com.g3.launcher.ui.component.G3Text
import com.g3.launcher.ui.component.option.OptionCheckBox
import com.g3.launcher.ui.component.option.OptionGroup
import com.g3.launcher.ui.component.option.OptionItem
import com.g3.launcher.ui.extension.hoverEffect
import com.g3.launcher.ui.theme.ColorOrange
import com.g3.launcher.ui.theme.ColorTextSecondary
import org.jetbrains.compose.resources.painterResource

@Composable
fun OtherOptionPane(
    viewModel: OtherOptionViewModel = remember { OtherOptionViewModel() }
) {
    val strings = LocalLanguage.current.strings
    val config = LocalConfig.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        if (viewModel.isCopying) {
            OptionItem(
                text = "Копирование директории ${viewModel.copyProgress}%",
                description = "",
                onClick = {},
            )
        } else {
            OptionCheckBox(
                text = "Играть с модами",
                description = "Будет создана копия игры для игры с модами.\n\n" +
                        "Сохранения обычной игры и с модами будут разделены.\n\n" +
                        "Рекомендуется при комбинировании игры запускать только через лаунчер иначе может быть путаница сохранений",
                checked = config.mods,
                onCheckedChange = viewModel::playWithMods
            )
        }

        if (config.mods) {
            Spacer(Modifier.height(20.dp))
            OptionItem(
                text = "Удалить директорию с модами",
                description = "Внимание! Будет удалена директория со всеми установленными модами. \n\nСохранения затронуты не будут",
                onClick = {
                    viewModel.removeModsDir()
                    viewModel.playWithMods(false)
                }
            )
        }

        Spacer(Modifier.height(48.dp))

        OptionGroup(
            text = "Основные директории",
            fontSize = 14.sp,
            color = ColorTextSecondary,
            fontWeight = FontWeight.Medium,
            itemSpacing = 20.dp
        ) {
            DirectoryItem(
                title = strings.gameDirectory,
                subtitle = viewModel.gameDir,
                onClick = viewModel::openGameDir
            )

            DirectoryItem(
                title = strings.savesDirectory,
                subtitle = viewModel.gameSaveDir,
                onClick = viewModel::openSaveDir
            )

            if (config.mods) {
                DirectoryItem(
                    title = "Директория игры с модами",
                    subtitle = viewModel.gameWithModsDir,
                    onClick = viewModel::openGameWithModsDir
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        OptionGroup(
            text = "Основные файлы игры",
            fontSize = 14.sp,
            color = ColorTextSecondary,
            fontWeight = FontWeight.Medium,
            itemSpacing = 20.dp
        ) {
            DirectoryItem(
                title = "ge3.ini",
                subtitle = viewModel.ge3IniPath,
                isFile = true,
                onClick = viewModel::openGe3
            )

            DirectoryItem(
                title = "mountlist.ini",
                subtitle = viewModel.mountlistIniPath,
                isFile = true,
                onClick = viewModel::openMountList
            )

            DirectoryItem(
                title = "UserOptions.ini",
                subtitle = viewModel.userOptionPath,
                isFile = true,
                onClick = viewModel::openUserOptions
            )
        }
    }
}

@Composable
private fun ColumnScope.DirectoryItem(
    title: String,
    subtitle: String,
    isFile: Boolean = false,
    onClick: () -> Unit,
) {
    var isHovered by remember { mutableStateOf(false) }

    val iconColorAnim by animateColorAsState(
        targetValue = if (isHovered) ColorOrange else Color.Black,
        label = "iconColor"
    )

    Box(
        modifier = Modifier
            .wrapContentSize()
            .align(Alignment.Start)
            .hoverEffect {
                isHovered = it
            }
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = {
                    onClick()
                }
            )
    ) {
        Image(
            painter = painterResource(if (isFile) Res.drawable.ic_file else Res.drawable.ic_folder),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(32.dp)
                .padding(top = 2.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = iconColorAnim,
                    spotColor = iconColorAnim,
                )
                .align(Alignment.TopStart)
        )

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .wrapContentSize()
                .padding(start = 40.dp)
        ) {
            G3Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                maxLines = 1,
                hoverable = true,
                hoverForce = isHovered,
                modifier = Modifier
                    .wrapContentWidth()
            )

            G3Text(
                text = subtitle,
                color = Color.Gray,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .wrapContentWidth()
            )
        }
    }
}
