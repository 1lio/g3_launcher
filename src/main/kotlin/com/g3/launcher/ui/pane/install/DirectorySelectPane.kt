package com.g3.launcher.ui.pane.install

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g3.launcher.g3_laucher.generated.resources.Res
import com.g3.launcher.g3_laucher.generated.resources.ic_folder
import com.g3.launcher.model.LocalLanguage
import com.g3.launcher.ui.extension.hoverEffect
import com.g3.launcher.ui.component.G3Text
import com.g3.launcher.ui.theme.ColorOrange
import com.g3.launcher.ui.theme.ColorText
import com.g3.launcher.ui.theme.ColorTextGray
import org.jetbrains.compose.resources.painterResource

@Composable
fun BoxScope.DirectorySelectPane(
    gameDir: String?,
    gameDirError: String?,
    gameSavesDir: String?,
    gameSaveError: String?,
    onClickGameDir: () -> Unit,
    onClickSavesDir: () -> Unit,
    onClickNext: () -> Unit,
) {
    val strings = LocalLanguage.current.strings
    var isEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(gameDir, gameDirError, gameSavesDir, gameSaveError) {
        isEnabled = gameDir != null && gameSavesDir != null
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .width(380.dp)
            .wrapContentHeight()
            .padding(24.dp)
            .align(alignment = Alignment.CenterEnd)
    ) {
        G3Text(
            text = strings.nextAction,
            enabled = isEnabled,
            hoverable = true,
            color = if (isEnabled) ColorText else ColorTextGray,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = isEnabled,
                    interactionSource = null,
                    indication = null,
                    onClick = onClickNext,
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.wrapContentSize()
        ) {
            DirectorySelector(
                title = strings.gameDirectory,
                subtitle = gameDir,
                error = gameDirError,
                onClick = onClickGameDir
            )

            DirectorySelector(
                title = strings.savesDirectory,
                subtitle = gameSavesDir,
                error = gameSaveError,
                onClick = onClickSavesDir
            )
        }
    }
}

@Composable
private fun ColumnScope.DirectorySelector(
    title: String,
    subtitle: String?,
    error: String?,
    onClick: () -> Unit,
) {
    val strings = LocalLanguage.current.strings

    val text = remember(subtitle, error, strings) {
        val text = subtitle ?: error?.let { "${strings.fileNotFound} : $it" } ?: strings.selectDirectory
        if (text.length > 40) {
            "...${text.takeLast(26)}"
        } else {
            text
        }
    }

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
            painter = painterResource(Res.drawable.ic_folder),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(24.dp)
                .padding(top = 2.dp)
                .shadow(
                    elevation = 24.dp,
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
                .padding(start = 32.dp)
        ) {
            G3Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                maxLines = 1,
                hoverable = true,
                hoverForce = isHovered,
                modifier = Modifier.wrapContentWidth()
            )

            G3Text(
                text = text,
                color = if (error != null || subtitle == null) ColorOrange else Color.Gray,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .wrapContentWidth()
            )
        }
    }
}
