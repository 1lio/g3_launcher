package com.g3.launcher.entity

import kotlinx.serialization.Serializable

@Serializable
class LauncherConfigJson(
    val version: String,
    val installed: Boolean,
    val language: String,
    val packages: List<String>,
    val gameDir: String?,
    val gameSaveDir: String?,
    val mods: Boolean,
    val modsConfig: Boolean,
)
