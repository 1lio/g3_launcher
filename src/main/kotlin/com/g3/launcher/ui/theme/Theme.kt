package com.g3.launcher.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun LauncherTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content,
    )
}
