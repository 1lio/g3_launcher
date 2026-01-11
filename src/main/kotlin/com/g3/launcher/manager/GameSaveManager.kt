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
        val vanillaSavesDir = File("$saveDir\\$VANILLA_SAVES_DIR")
        if (!vanillaSavesDir.exists()) {
            vanillaSavesDir.mkdir()
        }

        val modsSaveDir = File("$saveDir\\$MODS_SAVES_DIR")
        if (!modsSaveDir.exists()) {
            modsSaveDir.mkdir()
        }

        initDefaultIniFile(File(path))

        val currentVoiceLang = getVoiceLang()
        if (packages.contains(currentVoiceLang.key)) {
            setVoiceLanguage(currentVoiceLang)
            GameManager.setVoiceLanguage(currentVoiceLang)
        } else {
            setVoiceLanguage(G3Language.En)
            GameManager.setVoiceLanguage(G3Language.En)
        }

        val rootSaveDir = File(saveDir)

        copySaveFiles(
            sourceDir = rootSaveDir,
            targetDir = vanillaSavesDir,
            skipDirectories = listOf(VANILLA_SAVES_DIR, MODS_SAVES_DIR)
        )

        val preset = when (DeviceManager.AVAILABLE_RAM) {
            1 -> G3GraphicPreset.Low
            in 2..3 -> G3GraphicPreset.Medium
            4 -> G3GraphicPreset.High
            else -> G3GraphicPreset.VeryHigh
        }

        setScreenResolution(DeviceManager.SCREEN_RESOLUTION)
        setFpsLimit(false)
        setGraphicsPreset(preset)

        copySaveFiles(
            vanillaSavesDir,
            rootSaveDir,
            emptyList()
        )

        copyUserOptions(
            sourceDir = rootSaveDir,
            targetDir = vanillaSavesDir
        )

        copyUserOptions(
            sourceDir = rootSaveDir,
            targetDir = modsSaveDir
        )
    }

    fun setScreenResolution(screenResolution: ScreenResolution) {
        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Video",
            key = "Width",
            newValue = screenResolution.width.toString()
        )

        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Video",
            key = "Height",
            newValue = screenResolution.height.toString()
        )
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
        val presetName = when (preset) {
            is G3GraphicPreset -> preset.name
            else -> "VeryHigh"
        }

        IniFileManager.updateValue(
            filePath = path,
            section = "Options.Details",
            key = "Performance",
            newValue = presetName
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
        ) ?: "VeryHigh"

        return G3GraphicPreset.valueOf(value)
    }

    private fun copySaveFiles(sourceDir: File, targetDir: File, skipDirectories: List<String> = emptyList()) {
        if (!sourceDir.exists() || !sourceDir.isDirectory) return

        targetDir.mkdirs()

        // Копируем только файлы, исключая указанные папки
        sourceDir.listFiles()?.forEach { file ->
            if (file.name !in skipDirectories && !file.name.contains("Save_Backup", true)) {
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

    private fun initDefaultIniFile(
        file: File
    ) {
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(
            """
                [Options.Difficulty]
                ExtendedContent=false
                Difficulty=Medium
                AIMode=false
                [Options.Audio]
                MasterVolume=255
                VoiceVolume=255
                MusicVolume=54
                FXVolume=149
                AmbientVolume=202
                Speaker=2
                VoiceLanguage=English
                SubtitleLanguage=English
                Subtitle=true
                [Options.Video]
                WindowMode=2
                Resolution.Width=1920
                Resolution.Height=1080
                Resolution.RefreshRate=60
                VSync=1
                Brightness=0.000000
                Contrast=0.000000
                GammaRed=0.000000
                GammaGreen=0.000000
                GammaBlue=0.000000
                [Options.Details]
                Performance=VeryHigh
                [Options.Custom]
                DistanceHigh=3
                DistanceLow=3
                ObjectDetails=4
                TextureQuality=3
                TextureFilter=4
                VegetationQuality=3
                VegetationViewRange=4
                ShadowQuality=4
                LensFlare=0
                Bloom=2
                DepthOfField=1
                Antialiasing=1
                Noise=1
                Feedback=1
                ColorGrading=0
                [Options.Controls]
                MouseSensitivityX=0.500000
                MouseSensitivityY=0.500000
                MouseInvertX=false
                MouseInvertY=false
                MouseSmoothingX=0.500000
                MouseSmoothingY=0.500000
                AltCamera=false
                CamLookaroundInverse=true
                Controls=Standard
                [SessionKey.Forward]
                Key1.Type=0
                Key1.Offset=200
                Key2.Type=0
                Key2.Offset=17
                [SessionKey.Backward]
                Key1.Type=0
                Key1.Offset=208
                Key2.Type=0
                Key2.Offset=31
                [SessionKey.StrafeLeft]
                Key1.Type=0
                Key1.Offset=203
                Key2.Type=0
                Key2.Offset=30
                [SessionKey.StrafeRight]
                Key1.Type=0
                Key1.Offset=205
                Key2.Type=0
                Key2.Offset=32
                [SessionKey.TurnUp]
                Key1.Type=1
                Key1.Offset=10
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.TurnDown]
                Key1.Type=1
                Key1.Offset=9
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.RotateCamRight]
                Key1.Type=1
                Key1.Offset=7
                Key2.Type=0
                Key2.Offset=18
                [SessionKey.RotateCamLeft]
                Key1.Type=1
                Key1.Offset=8
                Key2.Type=0
                Key2.Offset=16
                [SessionKey.Walk]
                Key1.Type=0
                Key1.Offset=54
                Key2.Type=0
                Key2.Offset=42
                [SessionKey.WalkToggle]
                Key1.Type=0
                Key1.Offset=58
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.Up]
                Key1.Type=0
                Key1.Offset=82
                Key2.Type=0
                Key2.Offset=56
                [SessionKey.Down]
                Key1.Type=0
                Key1.Offset=157
                Key2.Type=0
                Key2.Offset=29
                [SessionKey.Use1]
                Key1.Type=1
                Key1.Offset=3
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.Use2]
                Key1.Type=1
                Key1.Offset=4
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.Plus]
                Key1.Type=0
                Key1.Offset=78
                Key2.Type=1
                Key2.Offset=11
                [SessionKey.Minus]
                Key1.Type=0
                Key1.Offset=74
                Key2.Type=1
                Key2.Offset=12
                [SessionKey.QuickUse0]
                Key1.Type=0
                Key1.Offset=11
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.QuickUse1]
                Key1.Type=0
                Key1.Offset=2
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.QuickUse2]
                Key1.Type=0
                Key1.Offset=3
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.QuickUse3]
                Key1.Type=0
                Key1.Offset=4
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.QuickUse4]
                Key1.Type=0
                Key1.Offset=5
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.QuickUse5]
                Key1.Type=0
                Key1.Offset=6
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.QuickUse6]
                Key1.Type=0
                Key1.Offset=7
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.QuickUse7]
                Key1.Type=0
                Key1.Offset=8
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.QuickUse8]
                Key1.Type=0
                Key1.Offset=9
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.QuickUse9]
                Key1.Type=0
                Key1.Offset=10
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.Confirm]
                Key1.Type=0
                Key1.Offset=28
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.InventoryMode]
                Key1.Type=0
                Key1.Offset=23
                Key2.Type=0
                Key2.Offset=14
                [SessionKey.JournalModeLog]
                Key1.Type=0
                Key1.Offset=38
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.JournalModeCharScreen]
                Key1.Type=0
                Key1.Offset=46
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.JournalModeMagBook]
                Key1.Type=0
                Key1.Offset=48
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.JournalModeMap]
                Key1.Type=0
                Key1.Offset=50
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.QuickLoad]
                Key1.Type=0
                Key1.Offset=67
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.QuickSave]
                Key1.Type=0
                Key1.Offset=63
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.WeaponMode]
                Key1.Type=0
                Key1.Offset=57
                Key2.Type=1
                Key2.Offset=5
                [SessionKey.Lock]
                Key1.Type=0
                Key1.Offset=19
                Key2.Type=0
                Key2.Offset=207
                [SessionKey.Look]
                Key1.Type=0
                Key1.Offset=80
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.FirstPerson]
                Key1.Type=0
                Key1.Offset=83
                Key2.Type=0
                Key2.Offset=20
                [SessionKey.ResetCamera]
                Key1.Type=0
                Key1.Offset=75
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.ToggleHUD]
                Key1.Type=0
                Key1.Offset=70
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.TakeAll]
                Key1.Type=0
                Key1.Offset=22
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.UsePotionHP]
                Key1.Type=0
                Key1.Offset=71
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.UsePotionMP]
                Key1.Type=0
                Key1.Offset=72
                Key2.Type=-1
                Key2.Offset=-1
                [SessionKey.UsePotionSP]
                Key1.Type=0
                Key1.Offset=73
                Key2.Type=-1
                Key2.Offset=-1
            """.trimIndent()
        )
    }
}
