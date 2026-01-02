package com.g3.launcher.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.g3.launcher.g3_laucher.generated.resources.Res
import com.g3.launcher.g3_laucher.generated.resources.ic_close
import com.g3.launcher.ui.extension.hoverEffect
import com.g3.launcher.ui.theme.ColorOrange
import org.jetbrains.compose.resources.painterResource

@Composable
fun CloseIcon(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {

    var isHovered by remember { mutableStateOf(false) }
    val color = if (isHovered) ColorOrange else Color.Black
    val shape = CrossShape()

    if (enabled) {
        Image(
            painter = painterResource(Res.drawable.ic_close),
            contentDescription = null,
            modifier = modifier
                .size(24.dp)
                .hoverEffect { isHovered = it }
                .shadow(
                    elevation = 12.dp,
                    shape = shape,
                    clip = false,
                    ambientColor = color,
                    spotColor = color
                )
                .clickable { onClick() }
        )
    }
}


class CrossShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val thickness = size.minDimension / 3f
            addRect(
                Rect(
                    left = 0f,
                    top = (size.height - thickness) / 2,
                    right = size.width,
                    bottom = (size.height + thickness) / 2
                )
            )
            addRect(
                Rect(
                    left = (size.width - thickness) / 2,
                    top = 0f,
                    right = (size.width + thickness) / 2,
                    bottom = size.height
                )
            )
        }
        return Outline.Generic(path)
    }
}