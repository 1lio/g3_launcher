package com.g3.launcher.manager

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.g3.launcher.entity.LauncherConfigJson
import com.g3.launcher.mapper.toConfig
import com.g3.launcher.mapper.toJson
import com.g3.launcher.model.G3Language
import com.g3.launcher.model.LauncherConfig
import com.g3.launcher.util.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

object LauncherManager {
    private const val CONFIG_FILE_NAME = "launcher.config"

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var config: LauncherConfig by mutableStateOf(value = loadConfig())
        private set

    val configState: State<LauncherConfig>
        get() = mutableStateOf(config)

    private fun createConfig(): LauncherConfig {
        val defaultConfig = LauncherConfig(
            installed = false,
            language = G3Language.fromKey(key = RegistryManager.getSystemLanguage()),
            packages = PackagesManager.getAvailablePackages(),
            gameDirPath = RegistryManager.getGameDir(),
            gameSaveDirPath = RegistryManager.getGameSaveDir(),
            mods = ModsManager.isAvailable(),
            availableUpdate = false,
        )

        saveConfig(defaultConfig)

        return defaultConfig
    }

    private fun loadConfig(): LauncherConfig {
        val file = File(CONFIG_FILE_NAME)

        return if (file.exists()) {
            json.decodeFromString<LauncherConfigJson>(string = file.readText())
                .toConfig(availableUpdate = false)
        } else {
            createConfig()
        }
    }

    fun updateConfig(update: LauncherConfig.() -> LauncherConfig) {
        val newConfig = config.update()
        config = newConfig

        scope.launch {
            saveConfig(newConfig)
        }
    }

    private fun saveConfig(config: LauncherConfig) {
        val data = json.encodeToString(LauncherConfigJson.serializer(), config.toJson())
        val file = File(CONFIG_FILE_NAME)
        file.writeText(data)
    }

    fun checkForUpdates() {
        scope.launch {
            val hasUpdate = LauncherVersionManager.isNewVersionAvailable()
            if (config.availableUpdate != hasUpdate) {
                updateConfig { copy(availableUpdate = hasUpdate) }
            }
        }
    }

    fun dispose() {
        scope.cancel()
    }
}
