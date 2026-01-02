package com.g3.launcher.manager

import java.awt.Window

object WindowManager {
    var mainWindow: Window? = null
    var optionsWindow: Window? = null

    fun showOptions(body: () -> Unit) {
        optionsWindow?.let {
            it.toFront()
            it.requestFocus()
        } ?: body()
    }
}