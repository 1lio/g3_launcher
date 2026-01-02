package com.g3.launcher.ui.pane.install

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g3.launcher.g3_laucher.generated.resources.Res
import com.g3.launcher.g3_laucher.generated.resources.ic_ok
import com.g3.launcher.model.LocalLanguage
import com.g3.launcher.ui.component.G3Text
import com.g3.launcher.ui.theme.ColorOrange
import com.g3.launcher.ui.theme.ColorText
import com.g3.launcher.ui.theme.ColorTextGray
import com.g3.launcher.ui.theme.ColorTextSecondary
import org.jetbrains.compose.resources.painterResource

@Composable
fun BoxScope.InstallationPane(
    steps: List<InstallViewModel.SetupStep>,
    progress: Int,
    download: Int,
) {
    val strings = LocalLanguage.current.strings
    val shapeBg = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)

    val animatedProgress by animateFloatAsState(
        targetValue = progress.toFloat() / 100,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        ),
        label = "progress_animation"
    )

    val animatedDownload by animateFloatAsState(
        targetValue = download.toFloat() / 100,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        ),
        label = "progress_animation2"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(380.dp)
            .wrapContentHeight()
            .padding(24.dp)
            .align(alignment = Alignment.CenterEnd)
    ) {
        G3Text(
            text = strings.installation,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )

        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(ColorTextGray, shapeBg)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .graphicsLayer {
                        clip = true
                        shape = shapeBg
                        scaleX = animatedDownload
                        transformOrigin = TransformOrigin(0f, 0.5f)
                    }
                    .background(ColorTextSecondary, shapeBg)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .graphicsLayer {
                        clip = true
                        shape = shapeBg
                        scaleX = animatedProgress
                        transformOrigin = TransformOrigin(0f, 0.5f)
                    }
                    .background(ColorOrange, shapeBg)
            )
        }

        Column {
            steps.forEach {
                Step(
                    title = when (it) {
                        is InstallViewModel.SetupStep.BaseDownload -> "Загрузка патчей"
                        is InstallViewModel.SetupStep.LocalizationDownload -> "Загрузка локализации (${it.count}/${it.total})"
                        is InstallViewModel.SetupStep.CleanGameDir -> "Очистка файлов игры"
                        is InstallViewModel.SetupStep.BaseInstall -> "Установка патчей"
                        is InstallViewModel.SetupStep.LocalizationInstall -> "Установка локализации (${it.count}/${it.total})"
                        is InstallViewModel.SetupStep.CreateBackup -> "Бекап сохранений"
                    },
                    progress = when (it) {
                        is InstallViewModel.SetupStep.BaseDownload -> it.progress
                        is InstallViewModel.SetupStep.BaseInstall -> it.progress
                        is InstallViewModel.SetupStep.LocalizationDownload -> it.progress
                        is InstallViewModel.SetupStep.LocalizationInstall -> it.progress
                        is InstallViewModel.SetupStep.CleanGameDir -> if (it.complete) 100 else 0
                        is InstallViewModel.SetupStep.CreateBackup -> if (it.complete) 100 else 0
                    }
                )
            }
        }
    }
}

@Composable
private fun Step(title: String, progress: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        G3Text(
            text = title,
            color = if (progress == 100) ColorTextGray else ColorText,
            fontSize = 14.sp
        )

        if (progress == 100) {
            Image(
                painter = painterResource(Res.drawable.ic_ok),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(12.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(1.dp),
                        ambientColor = ColorOrange,
                        spotColor = ColorOrange
                    )
            )
        } else {
            G3Text(
                text = "$progress%",
            )
        }
    }
}
