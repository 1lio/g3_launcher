package com.g3.launcher.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.g3.launcher.g3_laucher.generated.resources.Res
import com.g3.launcher.g3_laucher.generated.resources.ic_gift
import com.g3.launcher.model.LocalConfig
import com.g3.launcher.presentation.extension.hoverEffect
import com.g3.launcher.ui.theme.ColorOrange
import org.jetbrains.compose.resources.painterResource
import java.awt.Desktop
import java.net.URI

@Composable
fun UpdateButton(
    modifier: Modifier = Modifier
) {
    val config = LocalConfig.current
    var currentLang by remember { mutableStateOf(config.language) }
    var isHovered by remember { mutableStateOf(false) }

    val iconColorAnim by animateColorAsState(
        targetValue = if (isHovered) ColorOrange else Color.Black,
        label = "iconColor"
    )

    Box(
        modifier = modifier
            .wrapContentSize()
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_gift),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(36.dp)
                .hoverEffect { hovered ->
                    isHovered = hovered
                }
                .clickable(interactionSource = null, indication = null) {
                    Desktop.getDesktop().browse(URI("https://github.com/1lio/g3_laucher/releases/latest"))
                }
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(1.dp),
                    ambientColor = iconColorAnim,
                    spotColor = iconColorAnim
                )
                .align(Alignment.BottomStart)
        )

    }
}