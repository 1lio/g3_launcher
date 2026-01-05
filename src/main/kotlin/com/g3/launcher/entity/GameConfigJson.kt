package com.g3.launcher.entity

import com.g3.launcher.model.G3Language
import kotlinx.serialization.Serializable

@Serializable
class GameConfigJson(
    val textLang: String,
    val voiceLang: String,
    val subs: Boolean,
    val ruIntro: Boolean,
)