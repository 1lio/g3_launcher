package com.g3.launcher.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.g3.launcher.ui.theme.ColorOrange
import com.g3.launcher.ui.theme.ColorTextGray

@Composable
fun BoxScope.LinearProgress(progress: Int) {
    val shapeBg = RoundedCornerShape(0)

    val targetProgress = progress.toFloat() / 100f

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        ),
        label = "progress_animation"
    )

    Box(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .align(Alignment.BottomStart)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(ColorTextGray, shapeBg)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .graphicsLayer {
                    clip = true
                    shape = shapeBg
                    scaleX = animatedProgress
                    transformOrigin = TransformOrigin(0f, 0.5f)
                }
                .background(ColorOrange, shapeBg)
        )
    }
}