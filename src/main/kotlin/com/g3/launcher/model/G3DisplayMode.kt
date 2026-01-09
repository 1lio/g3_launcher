package com.g3.launcher.model

enum class G3DisplayMode(
    val value: String
): Preset {
    Windowed( "0") {
        override val key: String = "windowed"
    },
    BorderlessWindow("1") {
        override val key: String = "borderlessWindow"
    },
    Fullscreen( "2") {
        override val key: String = "fullscreen"
    }
}