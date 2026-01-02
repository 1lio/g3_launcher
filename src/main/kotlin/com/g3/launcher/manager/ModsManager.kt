package com.g3.launcher.manager

import java.io.File

object ModsManager {

    const val DIR_NAME: String = "GameWithMods"

    fun isAvailable(): Boolean {
        return RegistryManager.getGameDir()?.let {
            File("$it/${DIR_NAME}").exists()
        } ?: false
    }

}