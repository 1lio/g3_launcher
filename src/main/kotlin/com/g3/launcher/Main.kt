package com.g3.launcher

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "App",
    ) {
        Surface(
            color = Color.Transparent,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
        ) {
            Text("App is run!")
        }
    }
}
