package com.g3.launcher.manager

import com.g3.launcher.model.G3GraphicPreset
import com.g3.launcher.model.G3Language
import com.g3.launcher.model.GraphicsPreset

object GameSaveManager {
    private val dir: String? = LauncherManager.config.gameSaveDirPath
    private val path: String = dir?.let { "${it}\\UserOptions.ini" }
        ?: throw Exception("Failure: Not found GameSaveDir")

    fun firsConfig() {
        cleanSaveDir()

        val preset = when (DeviceManager.AVAILABLE_RAM) {
            1 -> G3GraphicPreset.Low
            in 2..3 -> G3GraphicPreset.Medium
            4 -> G3GraphicPreset.High
            else -> G3GraphicPreset.VeryHigh
        }

     /*   LauncherManager.updateConfig {
            copy(
                gPreset = preset
            )
        }*/

        setPreset(preset)
    }

    fun setTextLanguage(lang: G3Language): Boolean {
        return IniFileManager.updateValue(
            filePath = path,
            section = "Options.Audio",
            key = "SubtitleLanguage",
            newValue = lang.option
        )
    }

    fun getTextLang(): G3Language {
        val value = IniFileManager.readValue(
            filePath = path,
            section = "Options.Audio",
            key = "SubtitleLanguage",
        )

        return G3Language.entries.find { it.option == value } ?: G3Language.En
    }

    fun setVoiceLanguage(lang: G3Language): Boolean {
        return IniFileManager.updateValue(
            filePath = path,
            section = "Options.Audio",
            key = "VoiceLanguage",
            newValue = lang.option
        )
    }

    fun getVoiceLang(): G3Language {
        val value = IniFileManager.readValue(
            filePath = path,
            section = "Options.Audio",
            key = "VoiceLanguage",
        )

        return G3Language.entries.find { it.option == value } ?: G3Language.En
    }

    fun setSubs(enabled: Boolean): Boolean {
        return IniFileManager.updateValue(
            filePath = path,
            section = "Options.Audio",
            key = "Subtitle",
            newValue = enabled.toString()
        )
    }

    fun isShowSubs(): Boolean {
        return IniFileManager.readValue(
            filePath = path,
            section = "Options.Audio",
            key = "Subtitle",
        )?.toBoolean() ?: false
    }

    fun enableVsync(value: Boolean) {
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Video",
            key = "VSync",
            newValue = if (value) "1" else "0"
        )

      /*  LauncherManager.updateConfig { copy(vSync = value) }*/
    }

    fun setFpsLimit(value: Boolean) {
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Video",
            key = "RefreshRate",
            newValue = if (value) "60" else "${DeviceManager.FRAME_RATE}"
        )
        enableVsync(true)

     /*   LauncherManager.updateConfig { copy(fpsLimit = value) }*/
    }

    fun setAltCamera(value: Boolean) {
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Controls",
            key = "AltCamera",
            newValue = value.toString()
        )

       /* LauncherManager.updateConfig { copy(fpsLimit = value) }*/
    }

    fun cleanSaveDir() {

    }

    fun setPreset(preset: GraphicsPreset) {
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Details",
            key = "Performance",
            newValue = preset.name
        )

        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Custom",
            key = "DistanceHigh",
            newValue = preset.distanceHigh
        )
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Custom",
            key = "DistanceLow",
            newValue = preset.distanceLow
        )
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Custom",
            key = "ObjectDetails",
            newValue = preset.objectDetails
        )
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Custom",
            key = "TextureQuality",
            newValue = preset.textureQuality
        )
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Custom",
            key = "TextureFilter",
            newValue = preset.textureFilter
        )
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Custom",
            key = "VegetationQuality",
            newValue = preset.vegetationQuality
        )
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Custom",
            key = "VegetationViewRange",
            newValue = preset.vegetationViewRange
        )
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Custom",
            key = "ShadowQuality",
            newValue = preset.shadowQuality
        )
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Custom",
            key = "Bloom",
            newValue = preset.bloom
        )
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Custom",
            key = "DepthOfField",
            newValue = preset.depthOfField
        )
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Custom",
            key = "Antialiasing",
            newValue = preset.antialiasing
        )
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Custom",
            key = "Noise",
            newValue = preset.noise
        )
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Custom",
            key = "Feedback",
            newValue = preset.feedback
        )

    }

    fun getPreset(): G3GraphicPreset {
        val value = IniFileManager.readValue(
            filePath = path,
            section = "Options.Details",
            key = "Performance",
        ) ?: "Custom"

        return G3GraphicPreset.valueOf(value)
    }
}
