package com.g3.launcher.mapper

import com.g3.launcher.entity.LauncherConfigJson
import com.g3.launcher.model.LauncherConfig

fun LauncherConfigJson.toConfig(
    availableUpdate: Boolean
): LauncherConfig {
    return LauncherConfig(
        installed = installed,
        language = language,
        mods = mods,
        packages = packages,
        gameDirPath = gameDir,
        gameSaveDirPath = gameSaveDir,
        availableUpdate = availableUpdate,
    )
}

fun LauncherConfig.toJson(): LauncherConfigJson {
    return LauncherConfigJson(
        installed = installed,
        language = language,
        mods = mods,
        packages = packages,
        gameDir = gameDirPath,
        gameSaveDir = gameSaveDirPath,
    )
}
