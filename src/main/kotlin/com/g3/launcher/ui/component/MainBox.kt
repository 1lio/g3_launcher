package com.g3.launcher.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

enum class BgPosition { Start, Bottom }

@Composable
fun MainBox(
    backgroundRes: DrawableResource,
    backgroundPosition: BgPosition = BgPosition.Start,
    closeEnabled: Boolean = true,
    onClose: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val (backgroundSize, backgroundAlignment) = when (backgroundPosition) {
        BgPosition.Start -> 400.dp to Alignment.CenterStart
        BgPosition.Bottom -> 600.dp to Alignment.BottomCenter
    }

    val gradientAlignment = when (backgroundPosition) {
        BgPosition.Start -> Alignment.CenterEnd
        BgPosition.Bottom -> Alignment.TopCenter
    }

    var isCloseEnabled by remember { mutableStateOf(closeEnabled) }

    LaunchedEffect(closeEnabled) {
        isCloseEnabled = closeEnabled
    }

    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()

    ) {
        Box(
            modifier = Modifier
                .size(backgroundSize)
                .align(backgroundAlignment)
        ) {
            Image(
                painter = painterResource(backgroundRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = when (backgroundPosition) {
                            BgPosition.Start -> Brush.horizontalGradient(
                                listOf(Color.Transparent, Color.Black)
                            )

                            BgPosition.Bottom -> Brush.verticalGradient(
                                listOf(Color.Black, Color.Transparent)
                            )
                        }
                    )
                    .align(gradientAlignment)
            )
        }

        CloseIcon(
            enabled = isCloseEnabled,
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(all = 16.dp)
        )

        content()
    }
}