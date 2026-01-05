package com.g3.launcher.ui.window

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.g3.launcher.g3_laucher.generated.resources.Res
import com.g3.launcher.g3_laucher.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import java.awt.Point
import java.awt.Toolkit
import java.awt.Window
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.geom.RoundRectangle2D
import kotlin.math.min

@Composable
fun ApplicationScope.G3Window(
    width: Int,
    height: Int,
    title: String,
    content: @Composable FrameWindowScope.() -> Unit
) {
    val windowState = rememberWindowState(
        position = WindowPosition(Alignment.Center),
        size = getPreferredWindowSize(width, height)
    )
    var windowRef by remember { mutableStateOf<Window?>(null) }

    val density = LocalDensity.current
    val cornerRadiusDp = 8.dp
    val cornerRadiusPx = with(density) { cornerRadiusDp.toPx() * 2 }

    val shape = RoundedCornerShape(cornerRadiusDp)

    Window(
        onCloseRequest = ::exitApplication,
        title = title,
        state = windowState,
        icon = painterResource(Res.drawable.logo),
        resizable = false,
        undecorated = true,
        transparent = false,
        onPreviewKeyEvent = { false }
    ) {
        windowRef = this.window

        Surface(
            color = Color.Black,
            shape = shape,
        ) {
            content()
        }
    }

    LaunchedEffect(windowRef, cornerRadiusPx) {
        val win = windowRef ?: return@LaunchedEffect

        win.shape = RoundRectangle2D.Float(
            0f, 0f,
            win.width.toFloat(), win.height.toFloat(),
            cornerRadiusPx, cornerRadiusPx
        )

        setupWindowDragging(win)
    }
}

private fun getPreferredWindowSize(preferredW: Int, preferredH: Int): DpSize {
    val screen = Toolkit.getDefaultToolkit().screenSize
    val w = min(preferredW, (screen.width * 0.8f).toInt())
    val h = min(preferredH, (screen.height * 0.8f).toInt())
    return DpSize(w.dp, h.dp)
}

private fun setupWindowDragging(window: Window) {
    var mouseDownPoint: Point? = null
    var initialWindowPoint: Point? = null

    window.addMouseListener(object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent) {
            mouseDownPoint = e.locationOnScreen
            initialWindowPoint = window.locationOnScreen
        }

        override fun mouseReleased(e: MouseEvent) {
            mouseDownPoint = null
            initialWindowPoint = null
        }
    })

    window.addMouseMotionListener(object : MouseMotionAdapter() {
        override fun mouseDragged(e: MouseEvent) {
            val origin = mouseDownPoint ?: return
            val initialWin = initialWindowPoint ?: return
            val dx = e.locationOnScreen.x - origin.x
            val dy = e.locationOnScreen.y - origin.y
            val newX = initialWin.x + dx
            val newY = initialWin.y + dy
            window.setLocation(newX, newY)
        }
    })
}
