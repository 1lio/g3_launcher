package com.g3.launcher.manager

import com.g3.launcher.model.DistancePreset
import com.g3.launcher.model.G3Language
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
    private val gameDir = LauncherManager.config.gameDirPath
    private val g3Path = gameDir?.let { "${it}/Ini/ge3.ini" } ?: throw Exception("g3.ini not found")

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val videoMutex = Mutex()

    fun setGFonts(enabled: Boolean) {
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
}
