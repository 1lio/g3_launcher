package com.g3.launcher.model

import androidx.compose.runtime.Immutable

@Immutable
data class GameConfig(
    val textLang: G3Language,
    val voiceLang: G3Language,
    val subs: Boolean,
    val ruIntro: Boolean,
)