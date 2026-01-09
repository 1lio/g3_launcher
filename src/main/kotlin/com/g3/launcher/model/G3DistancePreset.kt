package com.g3.launcher.model

import androidx.compose.runtime.Immutable
import kotlin.Int

interface DistancePreset {

    interface Engine {
        val prefetchGridCellSize: Int
        val prefetchGridCellSizeLowPoly: Int
        val dOFStart: Float
        val dOFEnd: Float
        val dOFMaxBlur: Float
        val entityRoi: Int

        fun toIniMap(): Map<String, String> = mapOf(
            "Engine.Setup" to listOf(
                "Render.PrefetchGridCellSize" to prefetchGridCellSize.toString(),
                "Render.PrefetchGridCellSizeLowPoly" to prefetchGridCellSizeLowPoly.toString(),
                "Render.DOFStart" to dOFStart.toString(),
                "Render.DOFEnd" to dOFEnd.toString(),
                "Render.DOFMaxBlur" to dOFMaxBlur.toString(),
                "Entity.ROI" to entityRoi.toString(),
            )
        ).flatMap { (section, pairs) ->
            pairs.map { (key, value) ->
                "$section|$key" to value
            }
        }.toMap()
    }

    interface Sliders {
        val fFarClippingPlaneHigh: Float
        val fFarClippingPlaneMedium: Float
        val fFarClippingPlaneLow: Float
        val fFarClippingPlaneLowPolyMeshHigh: Float
        val fFarClippingPlaneLowPolyMeshMedium: Float
        val fFarClippingPlaneLowPolyMeshLow: Float
        val fViewDistanceVeryHigh: Float
        val fViewDistanceHigh: Float
        val fViewDistanceMedium: Float
        val fViewDistanceLow: Float
        val fScreenObjectDistanceCullingVeryHigh: Float
        val fProcessingRangeFadeOutRangeVeryHigh: Float
        val fRangedBaseLoDOffsetVeryHigh: Float
        val fGlobalVisualLoDFactorVeryHigh: Float
        val enuMeshLoDQualityStageVeryHigh: Float
        val enuAnimationLoDQualityStageVeryHigh: Float
        val fLowPolyObjectDistanceCullingVeryHigh: Float
        val fScreenObjectDistanceCullingHigh: Float
        val fProcessingRangeFadeOutRangeHigh: Float
        val fRangedBaseLoDOffsetHigh: Float
        val fGlobalVisualLoDFactorHigh: Float
        val enuMeshLoDQualityStageHigh: Float
        val enuAnimationLoDQualityStageHigh: Float
        val fLowPolyObjectDistanceCullingHigh: Float
        val fScreenObjectDistanceCullingMedium: Float
        val fProcessingRangeFadeOutRangeMedium: Float
        val fRangedBaseLoDOffsetMedium: Float
        val fGlobalVisualLoDFactorMedium: Float
        val enuMeshLoDQualityStageMedium: Float
        val enuAnimationLoDQualityStageMedium: Float
        val fLowPolyObjectDistanceCullingMedium: Float
        val fScreenObjectDistanceCullingLow: Float
        val fProcessingRangeFadeOutRangeLow: Float
        val fRangedBaseLoDOffsetLow: Float
        val fGlobalVisualLoDFactorLow: Float
        val enuMeshLoDQualityStageLow: Float
        val enuAnimationLoDQualityStageLow: Float
        val fLowPolyObjectDistanceCullingLow: Float

        fun toIniMap(): Map<String, String> = mapOf(
            "Option.Sliders" to listOf(
                "DistanceHigh.fFarClippingPlane_High" to fFarClippingPlaneHigh.toString(),
                "DistanceHigh.fFarClippingPlane_Medium" to fFarClippingPlaneMedium.toString(),
                "DistanceHigh.fFarClippingPlane_Low" to fFarClippingPlaneLow.toString(),
                "DistanceLow.fFarClippingPlaneLowPolyMesh_High" to fFarClippingPlaneLowPolyMeshHigh.toString(),
                "DistanceLow.fFarClippingPlaneLowPolyMesh_Medium" to fFarClippingPlaneLowPolyMeshMedium.toString(),
                "DistanceLow.fFarClippingPlaneLowPolyMesh_Low" to fFarClippingPlaneLowPolyMeshLow.toString(),
                "VegetationViewRange.fViewDistance_VeryHigh" to fViewDistanceVeryHigh.toString(),
                "VegetationViewRange.fViewDistance_High" to fViewDistanceHigh.toString(),
                "VegetationViewRange.fViewDistance_Medium" to fViewDistanceMedium.toString(),
                "VegetationViewRange.fViewDistance_Low" to fViewDistanceLow.toString(),
                "ObjectDetails.fScreenObjectDistanceCulling_VeryHigh" to fScreenObjectDistanceCullingVeryHigh.toString(),
                "ObjectDetails.fProcessingRangeFadeOutRange_VeryHigh" to fProcessingRangeFadeOutRangeVeryHigh.toString(),
                "ObjectDetails.fRangedBaseLoDOffset_VeryHigh" to fRangedBaseLoDOffsetVeryHigh.toString(),
                "ObjectDetails.fGlobalVisualLoDFactor_VeryHigh" to fGlobalVisualLoDFactorVeryHigh.toString(),
                "ObjectDetails.enuMeshLoDQualityStage_VeryHigh" to enuMeshLoDQualityStageVeryHigh.toString(),
                "ObjectDetails.enuAnimationLoDQualityStage_VeryHigh" to enuAnimationLoDQualityStageVeryHigh.toString(),
                "ObjectDetails.fLowPolyObjectDistanceCulling_VeryHigh" to fLowPolyObjectDistanceCullingVeryHigh.toString(),
                "ObjectDetails.fScreenObjectDistanceCulling_High" to fScreenObjectDistanceCullingHigh.toString(),
                "ObjectDetails.fProcessingRangeFadeOutRange_High" to fProcessingRangeFadeOutRangeHigh.toString(),
                "ObjectDetails.fRangedBaseLoDOffset_High" to fRangedBaseLoDOffsetHigh.toString(),
                "ObjectDetails.fGlobalVisualLoDFactor_High" to fGlobalVisualLoDFactorHigh.toString(),
                "ObjectDetails.enuMeshLoDQualityStage_High" to enuMeshLoDQualityStageHigh.toString(),
                "ObjectDetails.enuAnimationLoDQualityStage_High" to enuAnimationLoDQualityStageHigh.toString(),
                "ObjectDetails.fLowPolyObjectDistanceCulling_High" to fLowPolyObjectDistanceCullingHigh.toString(),
                "ObjectDetails.fScreenObjectDistanceCulling_Medium" to fScreenObjectDistanceCullingMedium.toString(),
                "ObjectDetails.fProcessingRangeFadeOutRange_Medium" to fProcessingRangeFadeOutRangeMedium.toString(),
                "ObjectDetails.fRangedBaseLoDOffset_Medium" to fRangedBaseLoDOffsetMedium.toString(),
                "ObjectDetails.fGlobalVisualLoDFactor_Medium" to fGlobalVisualLoDFactorMedium.toString(),
                "ObjectDetails.enuMeshLoDQualityStage_Medium" to enuMeshLoDQualityStageMedium.toString(),
                "ObjectDetails.enuAnimationLoDQualityStage_Medium" to enuAnimationLoDQualityStageMedium.toString(),
                "ObjectDetails.fLowPolyObjectDistanceCulling_Medium" to fLowPolyObjectDistanceCullingMedium.toString(),
                "ObjectDetails.fScreenObjectDistanceCulling_Low" to fScreenObjectDistanceCullingLow.toString(),
                "ObjectDetails.fProcessingRangeFadeOutRange_Low" to fProcessingRangeFadeOutRangeLow.toString(),
                "ObjectDetails.fRangedBaseLoDOffset_Low" to fRangedBaseLoDOffsetLow.toString(),
                "ObjectDetails.fGlobalVisualLoDFactor_Low" to fGlobalVisualLoDFactorLow.toString(),
                "ObjectDetails.enuMeshLoDQualityStage_Low" to enuMeshLoDQualityStageLow.toString(),
                "ObjectDetails.enuAnimationLoDQualityStage_Low" to enuAnimationLoDQualityStageLow.toString(),
                "ObjectDetails.fLowPolyObjectDistanceCulling_Low" to fLowPolyObjectDistanceCullingLow.toString(),

                )
        ).flatMap { (section, pairs) ->
            pairs.map { (key, value) ->
                "$section|$key" to value
            }
        }.toMap()
    }

    val engine: Engine
    val sliders: Sliders
}

@Immutable
enum class G3DistancePreset : DistancePreset, Preset {
    Default {
        override val key: String = "default"

        override val engine = object : DistancePreset.Engine {
            override val prefetchGridCellSize = 10_000
            override val prefetchGridCellSizeLowPoly = 34_000
            override val dOFStart = 2_000f
            override val dOFEnd = 12_000f
            override val dOFMaxBlur = 2.0f
            override val entityRoi = 6_000
        }

        override val sliders = object : DistancePreset.Sliders {
            // DistanceHigh
            override val fFarClippingPlaneHigh = 10000f
            override val fFarClippingPlaneMedium = 8000f
            override val fFarClippingPlaneLow = 6000f

            // DistanceLow
            override val fFarClippingPlaneLowPolyMeshHigh = 100000f
            override val fFarClippingPlaneLowPolyMeshMedium = 50000f
            override val fFarClippingPlaneLowPolyMeshLow = 25000f

            // Vegetation
            override val fViewDistanceVeryHigh = 7500f
            override val fViewDistanceHigh = 6500f
            override val fViewDistanceMedium = 5000f
            override val fViewDistanceLow = 4000f

            // ObjectDetails VeryHigh
            override val fScreenObjectDistanceCullingVeryHigh = 0.002f
            override val fProcessingRangeFadeOutRangeVeryHigh = 200f
            override val fRangedBaseLoDOffsetVeryHigh = 800f
            override val fGlobalVisualLoDFactorVeryHigh = 1.0f
            override val enuMeshLoDQualityStageVeryHigh = 2f
            override val enuAnimationLoDQualityStageVeryHigh = 2f
            override val fLowPolyObjectDistanceCullingVeryHigh = 0.01f

            // High
            override val fScreenObjectDistanceCullingHigh = 0.008f
            override val fProcessingRangeFadeOutRangeHigh = 300f
            override val fRangedBaseLoDOffsetHigh = 400f
            override val fGlobalVisualLoDFactorHigh = 0.01f
            override val enuMeshLoDQualityStageHigh = 2f
            override val enuAnimationLoDQualityStageHigh = 2f
            override val fLowPolyObjectDistanceCullingHigh = 0.01f

            // Medium
            override val fScreenObjectDistanceCullingMedium = 0.02f
            override val fProcessingRangeFadeOutRangeMedium = 500f
            override val fRangedBaseLoDOffsetMedium = 100f
            override val fGlobalVisualLoDFactorMedium = 0.01f
            override val enuMeshLoDQualityStageMedium = 0f
            override val enuAnimationLoDQualityStageMedium = 0f
            override val fLowPolyObjectDistanceCullingMedium = 0.01f

            // Low
            override val fScreenObjectDistanceCullingLow = 0.045f
            override val fProcessingRangeFadeOutRangeLow = 700f
            override val fRangedBaseLoDOffsetLow = -200f
            override val fGlobalVisualLoDFactorLow = 0.01f
            override val enuMeshLoDQualityStageLow = 0f
            override val enuAnimationLoDQualityStageLow = 0f
            override val fLowPolyObjectDistanceCullingLow = 0.01f
        }
    },

   /* Low {
        override val engine = object : DistancePreset.Engine {
            override val prefetchGridCellSize = 10_000
            override val prefetchGridCellSizeLowPoly = 34_000
            override val dOFStart = 2000f
            override val dOFEnd = 12000f
            override val dOFMaxBlur = 0.01f
        }

        override val sliders = object : DistancePreset.Sliders {

            override val fFarClippingPlaneHigh = 1000f
            override val fFarClippingPlaneMedium = 1000f
            override val fFarClippingPlaneLow = 1000f

            override val fFarClippingPlaneLowPolyMeshHigh = 10000f
            override val fFarClippingPlaneLowPolyMeshMedium = 10000f
            override val fFarClippingPlaneLowPolyMeshLow = 10000f

            override val fViewDistanceVeryHigh = 1000f
            override val fViewDistanceHigh = 1000f
            override val fViewDistanceMedium = 1000f
            override val fViewDistanceLow = 1000f

            override val fScreenObjectDistanceCullingVeryHigh = 0.045f
            override val fProcessingRangeFadeOutRangeVeryHigh = 700f
            override val fRangedBaseLoDOffsetVeryHigh = -200f
            override val fGlobalVisualLoDFactorVeryHigh = 0.01f
            override val enuMeshLoDQualityStageVeryHigh = 0f
            override val enuAnimationLoDQualityStageVeryHigh = 0f
            override val fLowPolyObjectDistanceCullingVeryHigh = 0.01f

            override val fScreenObjectDistanceCullingHigh = 0.045f
            override val fProcessingRangeFadeOutRangeHigh = 700f
            override val fRangedBaseLoDOffsetHigh = -200f
            override val fGlobalVisualLoDFactorHigh = 0.01f
            override val enuMeshLoDQualityStageHigh = 0f
            override val enuAnimationLoDQualityStageHigh = 0f
            override val fLowPolyObjectDistanceCullingHigh = 0.01f

            override val fScreenObjectDistanceCullingMedium = 0.045f
            override val fProcessingRangeFadeOutRangeMedium = 700f
            override val fRangedBaseLoDOffsetMedium = -200f
            override val fGlobalVisualLoDFactorMedium = 0.01f
            override val enuMeshLoDQualityStageMedium = 0f
            override val enuAnimationLoDQualityStageMedium = 0f
            override val fLowPolyObjectDistanceCullingMedium = 0.01f

            override val fScreenObjectDistanceCullingLow = 0.045f
            override val fProcessingRangeFadeOutRangeLow = 700f
            override val fRangedBaseLoDOffsetLow = -200f
            override val fGlobalVisualLoDFactorLow = 0.01f
            override val enuMeshLoDQualityStageLow = 0f
            override val enuAnimationLoDQualityStageLow = 0f
            override val fLowPolyObjectDistanceCullingLow = 0.01f
        }
    },*/

    Medium {
        override val key: String = "medium"

        override val engine = object : DistancePreset.Engine {
            override val prefetchGridCellSize = 12_000
            override val prefetchGridCellSizeLowPoly = 15_000
            override val dOFStart = 5000f
            override val dOFEnd = 7000f
            override val dOFMaxBlur = 4.0f
            override val entityRoi = 5_000
        }

        override val sliders = object : DistancePreset.Sliders {
            override val fFarClippingPlaneHigh = 18000f
            override val fFarClippingPlaneMedium = 14000f
            override val fFarClippingPlaneLow = 10000f

            override val fFarClippingPlaneLowPolyMeshHigh = 150000f
            override val fFarClippingPlaneLowPolyMeshMedium = 100000f
            override val fFarClippingPlaneLowPolyMeshLow = 100000f

            override val fViewDistanceVeryHigh = 18000f
            override val fViewDistanceHigh = 12000f
            override val fViewDistanceMedium = 5500f
            override val fViewDistanceLow = 4000f

            override val fScreenObjectDistanceCullingVeryHigh = 0.002f
            override val fProcessingRangeFadeOutRangeVeryHigh = 200f
            override val fRangedBaseLoDOffsetVeryHigh = 1200f
            override val fGlobalVisualLoDFactorVeryHigh = 1.0f
            override val enuMeshLoDQualityStageVeryHigh = 2f
            override val enuAnimationLoDQualityStageVeryHigh = 2f
            override val fLowPolyObjectDistanceCullingVeryHigh = 0.01f

            override val fScreenObjectDistanceCullingHigh = 0.008f
            override val fProcessingRangeFadeOutRangeHigh = 700f
            override val fRangedBaseLoDOffsetHigh = 800f
            override val fGlobalVisualLoDFactorHigh = 0.01f
            override val enuMeshLoDQualityStageHigh = 2f
            override val enuAnimationLoDQualityStageHigh = 0.01f
            override val fLowPolyObjectDistanceCullingHigh = 0.01f

            override val fScreenObjectDistanceCullingMedium = 0.02f
            override val fProcessingRangeFadeOutRangeMedium = 500f
            override val fRangedBaseLoDOffsetMedium = 100f
            override val fGlobalVisualLoDFactorMedium = 0.01f
            override val enuMeshLoDQualityStageMedium = 0f
            override val enuAnimationLoDQualityStageMedium = 0f
            override val fLowPolyObjectDistanceCullingMedium = 0.01f

            override val fScreenObjectDistanceCullingLow = 0.045f
            override val fProcessingRangeFadeOutRangeLow = 700f
            override val fRangedBaseLoDOffsetLow = -200f
            override val fGlobalVisualLoDFactorLow = 0.01f
            override val enuMeshLoDQualityStageLow = 0f
            override val enuAnimationLoDQualityStageLow = 0f
            override val fLowPolyObjectDistanceCullingLow = 0.01f
        }
    },

    High {
        override val key: String = "high"

        override val engine = object : DistancePreset.Engine {
            override val prefetchGridCellSize = 16000
            override val prefetchGridCellSizeLowPoly = 34000
            override val dOFStart = 5000f
            override val dOFEnd = 7000f
            override val dOFMaxBlur = 4.0f
            override val entityRoi = 6_000
        }

        override val sliders = object : DistancePreset.Sliders {

            override val fFarClippingPlaneHigh = 27000f
            override val fFarClippingPlaneMedium = 17000f
            override val fFarClippingPlaneLow = 10000f

            override val fFarClippingPlaneLowPolyMeshHigh = 140000f
            override val fFarClippingPlaneLowPolyMeshMedium = 100000f
            override val fFarClippingPlaneLowPolyMeshLow = 50000f

            override val fViewDistanceVeryHigh = 27000f
            override val fViewDistanceHigh = 17000f
            override val fViewDistanceMedium = 10000f
            override val fViewDistanceLow = 7000f

            override val fScreenObjectDistanceCullingVeryHigh = 0.002f
            override val fProcessingRangeFadeOutRangeVeryHigh = 200f
            override val fRangedBaseLoDOffsetVeryHigh = 1200f
            override val fGlobalVisualLoDFactorVeryHigh = 1.0f
            override val enuMeshLoDQualityStageVeryHigh = 2f
            override val enuAnimationLoDQualityStageVeryHigh = 2f
            override val fLowPolyObjectDistanceCullingVeryHigh = 0.01f

            override val fScreenObjectDistanceCullingHigh = 0.008f
            override val fProcessingRangeFadeOutRangeHigh = 700f
            override val fRangedBaseLoDOffsetHigh = 800f
            override val fGlobalVisualLoDFactorHigh = 0.01f
            override val enuMeshLoDQualityStageHigh = 2f
            override val enuAnimationLoDQualityStageHigh = 2f
            override val fLowPolyObjectDistanceCullingHigh = 0.01f

            override val fScreenObjectDistanceCullingMedium = 0.02f
            override val fProcessingRangeFadeOutRangeMedium = 500f
            override val fRangedBaseLoDOffsetMedium = 500f
            override val fGlobalVisualLoDFactorMedium = 0.01f
            override val enuMeshLoDQualityStageMedium = 0f
            override val enuAnimationLoDQualityStageMedium = 0f
            override val fLowPolyObjectDistanceCullingMedium = 0.01f

            override val fScreenObjectDistanceCullingLow = 0.045f
            override val fProcessingRangeFadeOutRangeLow = 700f
            override val fRangedBaseLoDOffsetLow = -600f
            override val fGlobalVisualLoDFactorLow = 0.01f
            override val enuMeshLoDQualityStageLow = 0f
            override val enuAnimationLoDQualityStageLow = 0f
            override val fLowPolyObjectDistanceCullingLow = 0.01f
        }
    },

}