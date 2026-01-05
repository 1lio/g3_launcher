package com.g3.launcher.model

interface GraphicsPreset {
    val distanceHigh: String
    val distanceLow: String
    val objectDetails: String
    val textureQuality: String
    val textureFilter: String
    val vegetationQuality: String
    val vegetationViewRange: String
    val shadowQuality: String
    val bloom: String
    val depthOfField: String
    val antialiasing: String
    val noise: String
    val feedback: String
    val name: String
}

enum class G3GraphicPreset(
    override val distanceHigh: String,
    override val distanceLow: String,
    override val objectDetails: String,
    override val textureQuality: String,
    override val textureFilter: String,
    override val vegetationQuality: String,
    override val vegetationViewRange: String,
    override val shadowQuality: String,
    override val bloom: String,
    override val depthOfField: String,
    override val antialiasing: String,
    override val noise: String,
    override val feedback: String
) : GraphicsPreset {
    Low(
        "1",
        "1",
        "1",
        "1",
        "0",
        "0",
        "1",
        "0",
        "0",
        "0",
        "0",
        "0",
        "0",
    ),
    Medium(
        "2",
        "2",
        "2",
        "2",
        "1",
        "1",
        "2",
        "1",
        "1",
        "0",
        "0",
        "0",
        "0",
    ),
    High(
        "3",
        "3",
        "3",
        "2",
        "2",
        "3",
        "3",
        "3",
        "1",
        "1",
        "0",
        "0",
        "0",
    ),
    VeryHigh(
        "3",
        "3",
        "4",
        "3",
        "4",
        "3",
        "4",
        "4",
        "2",
        "1",
        "1",
        "1",
        "1",
    ),
}

class Custom : GraphicsPreset {
    override val distanceHigh: String = "3"
    override val distanceLow: String = "3"
    override val objectDetails: String = "4"
    override val textureQuality: String = "3"
    override val textureFilter: String = "4"
    override val vegetationQuality: String = "3"
    override val vegetationViewRange: String = "4"
    override val shadowQuality: String = "4"
    override val bloom: String = "2"
    override val depthOfField: String = "1"
    override val antialiasing: String = "1"
    override val noise: String = "1"
    override val feedback: String = "1"
    override val name: String = "Custom"
}