package com.g3.launcher.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import com.g3.launcher.manager.LauncherManager

@Immutable
data class LauncherConfig(
    val installed: Boolean,
    val language: G3Language,
    val packages: List<String>,
    val gameDirPath: String?,
    val gameSaveDirPath: String?,
    val mods: Boolean,
    val availableUpdate: Boolean,
    val modsConfig: Boolean,
)

val LocalConfig = compositionLocalOf { LauncherManager.config }
val LocalLanguage = compositionLocalOf { LauncherManager.config.language }
