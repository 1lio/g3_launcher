package com.g3.launcher.manager

import com.g3.launcher.model.DistancePreset
import com.g3.launcher.model.G3DistancePreset
import com.g3.launcher.model.G3Language
import com.g3.launcher.model.Preset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object GameManager {

    private var isGameWithMods: Boolean = false

    private val gameDir = LauncherManager.config.gameDirPath

    private val path: String
        get() {
            val target = if (isGameWithMods) "\\GameWithMods" else ""
            return gameDir.let { "${it}${target}" }
        }

    private val g3Path
        get() = path.let { "${it}\\Ini\\ge3.ini" }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val videoMutex = Mutex()

    fun setGameMode(mods: Boolean) {
        isGameWithMods = mods
    }

    fun setTestMode(enabled: Boolean) {
        IniFileManager.updateValue(
            filePath = g3Path,
            section = "Game",
            key = "TestMode",
            newValue = enabled.toString()
        )
    }

    fun isTestMode(): Boolean {
        val value = IniFileManager.readValue(
            filePath = g3Path,
            section = "Game",
            key = "TestMode",
        )
        return value == "true"
    }

    fun setQuickLoot(enabled: Boolean) {
        IniFileManager.updateValue(
            filePath = g3Path,
            section = "Game",
            key = "QuickLoot",
            newValue = enabled.toString()
        )
    }

    fun isQuickLoot(): Boolean {
        val value = IniFileManager.readValue(
            filePath = g3Path,
            section = "Game",
            key = "QuickLoot",
        )
        return value == "true"
    }

    fun setLockGame(enabled: Boolean) {
        IniFileManager.updateValue(
            filePath = g3Path,
            section = "Game",
            key = "LocksPlugin",
            newValue = if (enabled) "1" else "0"
        )
    }

    fun isLockGame(): Boolean {
        val value = IniFileManager.readValue(
            filePath = g3Path,
            section = "Game",
            key = "LocksPlugin",
        )
        return value == "1"
    }

    fun setExtendedContent(enabled: Boolean) {
        IniFileManager.updateValue(
            filePath = g3Path,
            section = "Game",
            key = "ExtendedContent",
            newValue = if (enabled) "1" else "0"
        )
    }

    fun isExtendedContent(): Boolean {
        val value = IniFileManager.readValue(
            filePath = g3Path,
            section = "Game",
            key = "ExtendedContent",
        )
        return value == "1"
    }

    fun setGFont(enabled: Boolean) {
        IniFileManager.updateValue(
            filePath = g3Path,
            section = "Engine.Setup",
            key = "GUI.DefaultFont",
            newValue = if (enabled) "Gothic3" else "Comic Sans MS"
        )

        IniFileManager.updateValue(
            filePath = g3Path,
            section = "Engine.Setup",
            key = "GUI.DefaultFontBold",
            newValue = if (enabled) "true" else "default"
        )
    }

    fun isGFont(): Boolean {
        val value = IniFileManager.readValue(
            filePath = g3Path,
            section = "Engine.Setup",
            key = "GUI.DefaultFont",
        )
        return value == "Gothic3"
    }

    fun setHintAttackDuration(value: Int) {
        IniFileManager.updateValue(
            filePath = g3Path,
            section = "Game",
            key = "MinHitDuration",
            newValue = value.toString()
        )
    }

    fun getHintAttackDuration(): Int {
        val value = IniFileManager.readValue(
            filePath = g3Path,
            section = "Game",
            key = "MinHitDuration",
        )
        return value?.toIntOrNull() ?: 6
    }

    fun setCombatExp(value: Int) {
        IniFileManager.updateValue(
            filePath = g3Path,
            section = "Game",
            key = "XPModifier",
            newValue = value.toString()
        )
    }

    fun getCombatExp(): Int {
        val value = IniFileManager.readValue(
            filePath = g3Path,
            section = "Game",
            key = "XPModifier",
        )
        return value?.toIntOrNull() ?: 6
    }

    fun setQuestExp(value: Int) {
        IniFileManager.updateValue(
            filePath = g3Path,
            section = "Game",
            key = "QuestXPModifier",
            newValue = value.toString()
        )
    }

    fun getQuestExp(): Int {
        val value = IniFileManager.readValue(
            filePath = g3Path,
            section = "Game",
            key = "QuestXPModifier",
        )
        return value?.toIntOrNull() ?: 6
    }

    fun useRuIntro(enabled: Boolean) {
        scope.launch {
            videoMutex.withLock {
                val intro = File(gameDir, "Data/Video/G3_Intro.bik")
                val introRu = File(gameDir, "Data/Video/G3_IntroRu.bik")
                val temp = File(gameDir, "Data/Video/G3_Intro.tmp")

                if (!intro.exists() || !introRu.exists()) {
                    return@withLock
                }

                if (enabled) {
                    swapFiles(intro, introRu, temp)
                } else {
                    swapFiles(introRu, intro, temp)
                }
            }
        }
    }

    private fun swapFiles(a: File, b: File, temp: File) {
        Files.move(a.toPath(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING)
        Files.move(b.toPath(), a.toPath(), StandardCopyOption.REPLACE_EXISTING)
        Files.move(temp.toPath(), b.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    fun isUseRuIntro(): Boolean {
        val intro = File(gameDir, "Data/Video/G3_Intro.bik").length()
        val introRu = File(gameDir, "Data/Video/G3_IntroRu.bik").length()

        return introRu < intro
    }

    fun firstConfig() {
        IniFileManager.updateValue(
            filePath = g3Path,
            section = "Engine.Setup",
            key = "Threads.Priority",
            newValue = "2"
        )
        IniFileManager.updateValue(
            filePath = g3Path,
            section = "Engine.Setup",
            key = "Physics.Threads",
            newValue = "${DeviceManager.AVAILABLE_PROCESSOR}"
        )

        setDistancePreset(G3DistancePreset.Medium)
    }

    fun setDistancePreset(preset: DistancePreset) {
        for ((sectionKey, value) in preset.engine.toIniMap()) {
            val (section, key) = sectionKey.split("|")

            IniFileManager.updateValue(
                filePath = g3Path,
                section = section,
                key = key,
                newValue = value
            )
        }

        for ((sectionKey, value) in preset.sliders.toIniMap()) {
            val (section, key) = sectionKey.split("|")

            IniFileManager.updateValue(
                filePath = g3Path,
                section = section,
                key = key,
                newValue = value
            )
        }
    }

    fun isSkipIntro(): Boolean {
        val logoIni = File("$path\\Ini\\logo.ini")
        return logoIni.length() == 0L
    }

    fun skipIntro(value: Boolean) {
        val logoIni = File("$path\\Ini\\logo.ini")
        if (value) {
            logoIni.writeText("")
        } else {
            logoIni.writeText(
                """
                    G3_Logo_01.bik
                    Publisher.bik
                    CPT.bik
                """.trimIndent()
            )
        }
    }

    fun getCurrentPreset(): DistancePreset {
        return object : DistancePreset {
            override val engine = object : DistancePreset.Engine {
                override val prefetchGridCellSize: Int = IniFileManager.readValue(
                    filePath = g3Path,
                    section = "Engine.Setup",
                    key = "Render.PrefetchGridCellSize",
                )?.toIntOrNull() ?: 0
                override val prefetchGridCellSizeLowPoly: Int = IniFileManager.readValue(
                    filePath = g3Path,
                    section = "Engine.Setup",
                    key = "Render.PrefetchGridCellSizeLowPoly",
                )?.toIntOrNull() ?: 0
                override val dOFStart: Float = IniFileManager.readValue(
                    filePath = g3Path,
                    section = "Engine.Setup",
                    key = "Render.DOFStart",
                )?.toFloatOrNull() ?: 0f
                override val dOFEnd: Float = IniFileManager.readValue(
                    filePath = g3Path,
                    section = "Engine.Setup",
                    key = "Render.DOFEnd",
                )?.toFloatOrNull() ?: 0f
                override val dOFMaxBlur: Float = IniFileManager.readValue(
                    filePath = g3Path,
                    section = "Engine.Setup",
                    key = "Render.DOFMaxBlur",
                )?.toFloatOrNull() ?: 0f
                override val entityRoi: Int = IniFileManager.readValue(
                    filePath = g3Path,
                    section = "Engine.Setup",
                    key = "Entity.ROI",
                )?.toIntOrNull() ?: 0
            }

            override val sliders = object : DistancePreset.Sliders {
                override val fFarClippingPlaneHigh: Float = 0f
                override val fFarClippingPlaneMedium: Float = 0f
                override val fFarClippingPlaneLow: Float = 0f
                override val fFarClippingPlaneLowPolyMeshHigh: Float = 0f
                override val fFarClippingPlaneLowPolyMeshMedium: Float = 0f
                override val fFarClippingPlaneLowPolyMeshLow: Float = 0f
                override val fViewDistanceVeryHigh: Float = 0f
                override val fViewDistanceHigh: Float = 0f
                override val fViewDistanceMedium: Float = 0f
                override val fViewDistanceLow: Float = 0f
                override val fScreenObjectDistanceCullingVeryHigh: Float = 0f
                override val fProcessingRangeFadeOutRangeVeryHigh: Float = 0f
                override val fRangedBaseLoDOffsetVeryHigh: Float = 0f
                override val fGlobalVisualLoDFactorVeryHigh: Float = 0f
                override val enuMeshLoDQualityStageVeryHigh: Float = 0f
                override val enuAnimationLoDQualityStageVeryHigh: Float = 0f
                override val fLowPolyObjectDistanceCullingVeryHigh: Float = 0f
                override val fScreenObjectDistanceCullingHigh: Float = 0f
                override val fProcessingRangeFadeOutRangeHigh: Float = 0f
                override val fRangedBaseLoDOffsetHigh: Float = 0f
                override val fGlobalVisualLoDFactorHigh: Float = 0f
                override val enuMeshLoDQualityStageHigh: Float = 0f
                override val enuAnimationLoDQualityStageHigh: Float = 0f
                override val fLowPolyObjectDistanceCullingHigh: Float = 0f
                override val fScreenObjectDistanceCullingMedium: Float = 0f
                override val fProcessingRangeFadeOutRangeMedium: Float = 0f
                override val fRangedBaseLoDOffsetMedium: Float = 0f
                override val fGlobalVisualLoDFactorMedium: Float = 0f
                override val enuMeshLoDQualityStageMedium: Float = 0f
                override val enuAnimationLoDQualityStageMedium: Float = 0f
                override val fLowPolyObjectDistanceCullingMedium: Float = 0f
                override val fScreenObjectDistanceCullingLow: Float = 0f
                override val fProcessingRangeFadeOutRangeLow: Float = 0f
                override val fRangedBaseLoDOffsetLow: Float = 0f
                override val fGlobalVisualLoDFactorLow: Float = 0f
                override val enuMeshLoDQualityStageLow: Float = 0f
                override val enuAnimationLoDQualityStageLow: Float = 0f
                override val fLowPolyObjectDistanceCullingLow: Float = 0f
            }
        }
    }

    fun setVoiceLanguage(lang: G3Language) {
        val iniPath = File(gameDir, "Ini/mountlist.ini")
        val path = iniPath.absolutePath

        // Сопоставление языков с ключами в файле
        val voiceFileMap = mapOf(
            G3Language.En to "Data/Speech_english",
            G3Language.Fr to "Data/Speech_french",
            G3Language.De to "Data/Speech_german",
            G3Language.Pl to "Data/Speech_polish",
            G3Language.Ru to "Data/Speech_russian"
        )

        val targetVoiceFile = voiceFileMap[lang] ?: return

        try {
            // Читаем файл
            val lines = File(path).readLines().toMutableList()
            var modified = false

            // Обрабатываем все строки с голосовыми пакетами
            val voicePrefixes = listOf(
                "Data/Speech_english",
                "Data/Speech_french",
                "Data/Speech_german",
                "Data/Speech_polish",
                "Data/Speech_russian"
            )

            for (i in lines.indices) {
                val line = lines[i].trim()
                voicePrefixes.forEach { prefix ->
                    if (line.startsWith(prefix) || line.startsWith(";$prefix")) {
                        val isTargetLanguage = prefix == targetVoiceFile

                        if (isTargetLanguage) {
                            // Раскомментируем строку для целевого языка
                            if (line.startsWith(";")) {
                                lines[i] = line.substring(1) // Убираем точку с запятой
                                modified = true
                            }
                        } else {
                            // Закомментируем строки для других языков
                            if (!line.startsWith(";")) {
                                lines[i] = ";$line"
                                modified = true
                            }
                        }
                    }
                }
            }

            if (modified) {
                File(path).writeText(lines.joinToString("\n"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDistancePreset(): G3DistancePreset {
        val preset = getCurrentPreset()
        val size = preset.engine.prefetchGridCellSize

        return when (size) {
            10000 -> G3DistancePreset.Default
            12000 -> G3DistancePreset.Medium
            16000 -> G3DistancePreset.High
            else -> G3DistancePreset.Default
        }
    }
}
