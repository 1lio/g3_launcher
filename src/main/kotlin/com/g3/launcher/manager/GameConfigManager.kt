package com.g3.launcher.manager

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.g3.launcher.entity.GameConfigJson
import com.g3.launcher.mapper.toConfig
import com.g3.launcher.mapper.toJson
import com.g3.launcher.model.GameConfig
import com.g3.launcher.util.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

object GameConfigManager {

    private const val CONFIG_FILE_NAME = "game.config"

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gameConfig: GameConfig by mutableStateOf(value = loadConfig())
        private set

    val configState: State<GameConfig>
        get() = mutableStateOf(gameConfig)

    private fun createConfig(): GameConfig {
        val defaultConfig = GameConfig(
            textLang = GameSaveManager.getTextLang(),
            voiceLang = GameSaveManager.getVoiceLang(),
            subs = GameSaveManager.isShowSubs(),
            ruIntro = false
        )

        saveConfig(defaultConfig)

        return defaultConfig
    }

    private fun loadConfig(): GameConfig {
        val file = File(CONFIG_FILE_NAME)

        return if (file.exists()) {
            json.decodeFromString<GameConfigJson>(string = file.readText()).toConfig()
        } else {
            createConfig()
        }
    }

    fun updateConfig(update: GameConfig.() -> GameConfig) {
        val newConfig = gameConfig.update()
        gameConfig = newConfig

        scope.launch {
            saveConfig(newConfig)
        }
    }

    private fun saveConfig(config: GameConfig) {
        val data = json.encodeToString(GameConfigJson.serializer(), config.toJson())
        val file = File(CONFIG_FILE_NAME)
        file.writeText(data)
    }

    fun dispose() {
        scope.cancel()
    }
}