package com.g3.launcher.manager

import com.g3.launcher.model.G3DisplayMode
import com.g3.launcher.model.G3GraphicPreset
import com.g3.launcher.model.G3Language
import com.g3.launcher.model.GraphicsPreset
import java.io.File

object GameSaveManager {

    private const val USER_OPTIONS = "UserOptions.ini"

    private val SAVE_FILE_EXTENSIONS = setOf(
        "g3savcp",
        "g3savcpx",
        "g3savcpdat",
        "g3savcpxdat",
    )

    private val IMPORTANT_FILES = setOf(
        "Shader.Cache",
        USER_OPTIONS,
    )

    private const val VANILLA_SAVES_DIR = "Vanilla"
    private const val MODS_SAVES_DIR = "WithMods"

    private val saveDir: String = LauncherManager.config.gameSaveDirPath ?: ""
    private var isGameWithMods: Boolean = false

    private val path: String
        get() {
            val target = if (isGameWithMods) MODS_SAVES_DIR else VANILLA_SAVES_DIR
            return saveDir.let { "${it}\\${target}\\$USER_OPTIONS" }
        }

    fun setGameMode(withMods: Boolean) {
        isGameWithMods = withMods
    }

    fun setExtendedContent(enabled: Boolean) {
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Difficulty",
            key = "ExtendedContent",
            newValue = enabled.toString()
        )
    }


    fun firstConfig(packages: List<String>) {
        val rootSaveDir = File(saveDir)

        val vanillaSavesDir = File(saveDir, VANILLA_SAVES_DIR)
        if (!vanillaSavesDir.exists()) {
            vanillaSavesDir.mkdir()
        }

        val modsSaveDir = File(saveDir, MODS_SAVES_DIR)
        if (!modsSaveDir.exists()) {
            modsSaveDir.mkdir()
        }

        val currentVoiceLang = getVoiceLang().key
        if (!packages.contains(currentVoiceLang)) {
            setVoiceLanguage(G3Language.En)
        }

        copySaveFiles(
            sourceDir = rootSaveDir,
            targetDir = vanillaSavesDir,
            skipDirectories = listOf(VANILLA_SAVES_DIR, MODS_SAVES_DIR)
        )

        copyUserOptions(
            sourceDir = rootSaveDir,
            targetDir = vanillaSavesDir
        )

        copyUserOptions(
            sourceDir = rootSaveDir,
            targetDir = modsSaveDir
        )

        val preset = when (DeviceManager.AVAILABLE_RAM) {
            1 -> G3GraphicPreset.Low
            in 2..3 -> G3GraphicPreset.Medium
            4 -> G3GraphicPreset.High
            else -> G3GraphicPreset.VeryHigh
        }

        setGraphicsPreset(preset)
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
    }

    fun isVsync(): Boolean {
        val value = IniFileManager.readValue(
            filePath = path,
            section = "Options.Video",
            key = "VSync",
        )
        return value == "1"
    }

    fun setFpsLimit(value: Boolean) {
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Video",
            key = "RefreshRate",
            newValue = if (value) "60" else "${DeviceManager.FRAME_RATE}"
        )
        enableVsync(true)
    }

    fun isFpsLimit(): Boolean {
        val value = IniFileManager.readValue(
            filePath = path,
            section = "Options.Video",
            key = "RefreshRate",
        )?.toIntOrNull() ?: 60

        val rate = value == 60
        return rate && isVsync()
    }

    fun setAltCamera(value: Boolean) {
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Controls",
            key = "AltCamera",
            newValue = value.toString()
        )
    }

    fun isAltCamera(): Boolean {
        val value = IniFileManager.readValue(
            filePath = path,
            section = "Options.Controls",
            key = "AltCamera",
        )
        return value == "true"
    }

    fun setAltAI(value: Boolean) {
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Difficulty",
            key = "AIMode",
            newValue = value.toString()
        )
    }

    fun isAltAI(): Boolean {
        val value = IniFileManager.readValue(
            filePath = path,
            section = "Options.Difficulty",
            key = "AIMode",
        )
        return value == "true"
    }

    fun setDisplayMode(mode: G3DisplayMode) {
        val value = when (mode) {
            G3DisplayMode.Windowed -> 0
            G3DisplayMode.BorderlessWindow -> 1
            G3DisplayMode.Fullscreen -> 2
        }

        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Video",
            key = "WindowMode",
            newValue = value.toString()
        )
    }

    fun getDisplayMode(): G3DisplayMode {
        val value = IniFileManager.readValue(
            filePath = path,
            section = "Options.Video",
            key = "WindowMode",
        )?.toIntOrNull() ?: 2

        return when (value) {
            0 -> G3DisplayMode.Windowed
            1 -> G3DisplayMode.BorderlessWindow
            else -> G3DisplayMode.Fullscreen
        }
    }

    fun setGraphicsPreset(preset: GraphicsPreset) {
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

    fun getGraphicsPreset(): G3GraphicPreset {
        val value = IniFileManager.readValue(
            filePath = path,
            section = "Options.Details",
            key = "Performance",
        ) ?: "Custom"

        return G3GraphicPreset.valueOf(value)
    }

    private fun copySaveFiles(sourceDir: File, targetDir: File, skipDirectories: List<String> = emptyList()) {
        if (!sourceDir.exists() || !sourceDir.isDirectory) return

        targetDir.mkdirs()

        // Копируем только файлы, исключая указанные папки
        sourceDir.listFiles()?.forEach { file ->
            if (file.name !in skipDirectories) {
                val targetFile = File(targetDir, file.name)
                try {
                    if (file.isFile && shouldCopyFile(file)) { // Копируем только файлы
                        file.copyTo(targetFile, overwrite = true)
                    }
                } catch (_: Exception) {
                    // Игнорируем ошибки копирования
                }
            }
        }
    }

    private fun shouldCopyFile(file: File): Boolean {
        if (!file.isFile) return false // Не копируем папки

        val name = file.name
        val extension = file.extension.lowercase()

        // Проверяем расширения .g3savcp, .g3savcpx, .g3savcpdat, .g3savcpxdat
        val isSaveFile = extension in SAVE_FILE_EXTENSIONS

        // Проверяем важные файлы
        val isImportantFile = name in IMPORTANT_FILES

        return isSaveFile || isImportantFile
    }

    private fun copyUserOptions(
        sourceDir: File,
        targetDir: File,
    ) {
        val options = File("$sourceDir\\$USER_OPTIONS")
        val targetFile = File("$targetDir\\$USER_OPTIONS")
        options.copyTo(targetFile, overwrite = true)
    }
}
