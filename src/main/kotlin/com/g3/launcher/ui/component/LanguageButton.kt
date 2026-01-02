package com.g3.launcher.ui.component

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g3.launcher.g3_laucher.generated.resources.Res
import com.g3.launcher.g3_laucher.generated.resources.ic_translate
import com.g3.launcher.manager.LauncherManager
import com.g3.launcher.model.G3Language
import com.g3.launcher.model.LocalConfig
import com.g3.launcher.ui.extension.hoverEffect
import com.g3.launcher.ui.theme.ColorOrange
import org.jetbrains.compose.resources.painterResource

@Composable
fun LanguageButton(
    modifier: Modifier = Modifier
) {
    val config = LocalConfig.current
    var currentLang by remember { mutableStateOf(config.language) }
    var isOpened by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }

    val iconColorAnim by animateColorAsState(
        targetValue = if (isHovered && !isOpened) ColorOrange else Color.Black,
        label = "iconColor"
    )

    Box(
        modifier = modifier
            .wrapContentSize()
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_translate),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(36.dp)
                .hoverEffect { hovered ->
                    isHovered = hovered
                }
                .clickable(interactionSource = null, indication = null) {
                    isOpened = !isOpened
                }
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = iconColorAnim,
                    spotColor = iconColorAnim
                )
                .align(Alignment.BottomStart)
        )

        AnimatedVisibility(
            visible = isOpened,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 48.dp)
                    .background(Color.Black.copy(alpha = .5f), RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
            ) {
                G3Language.entries.forEach { lang ->
                    val selected = lang == currentLang
                    G3Text(
                        hoverable = !selected,
                        text = lang.title,
                        textAlign = TextAlign.Center,
                        color = if (selected) ColorOrange else Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable(interactionSource = null, indication = null) {
                                currentLang = lang
                                isOpened = false
                                LauncherManager.updateConfig { copy(language = lang) }
                            }
                    )
                }
            }
        }
    }
}