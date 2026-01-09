package com.g3.launcher.ui.pane.option.graphic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.g3.launcher.manager.GameManager
import com.g3.launcher.manager.GameSaveManager
import com.g3.launcher.model.G3DisplayMode
import com.g3.launcher.model.G3DistancePreset
import com.g3.launcher.model.G3GraphicPreset

class GraphicsOptionViewModel {

    var currentDisplayMode: G3DisplayMode by mutableStateOf(GameSaveManager.getDisplayMode())
        private set

    var currentGraphicsPreset: G3GraphicPreset by mutableStateOf(GameSaveManager.getGraphicsPreset())
        private set

    var currentDistancePreset: G3DistancePreset by mutableStateOf(GameManager.getDistancePreset())
        private set

    var vSync: Boolean by mutableStateOf(GameSaveManager.isVsync())
        private set

    var fpsLimit: Boolean by mutableStateOf(GameSaveManager.isFpsLimit())
        private set

    fun updateSettingsType(mods: Boolean) {
        GameManager.setGameMode(mods)
        GameSaveManager.setGameMode(mods)

        currentDisplayMode = GameSaveManager.getDisplayMode()
        currentGraphicsPreset = GameSaveManager.getGraphicsPreset()
        currentDistancePreset = GameManager.getDistancePreset()
        vSync = GameSaveManager.isVsync()
        this@GraphicsOptionViewModel.fpsLimit = GameSaveManager.isFpsLimit()
    }

    fun setDisplayMode(mode: G3DisplayMode) {
        GameSaveManager.setDisplayMode(mode)
        currentDisplayMode = mode
    }

    fun setGraphicsPreset(preset: G3GraphicPreset) {
        GameSaveManager.setGraphicsPreset(preset)
        currentGraphicsPreset = preset
    }

    fun setDistancePreset(preset: G3DistancePreset) {
        GameManager.setDistancePreset(preset)
        currentDistancePreset = preset
    }

    fun vsync(value: Boolean) {
        GameSaveManager.enableVsync(value)
        vSync = value
    }

    fun fpsLimit(value: Boolean) {
        GameSaveManager.setFpsLimit(value)
        this@GraphicsOptionViewModel.fpsLimit = value
    }
}
