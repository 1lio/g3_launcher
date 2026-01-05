package com.g3.launcher.mapper

import com.g3.launcher.entity.GameConfigJson
import com.g3.launcher.model.G3Language
import com.g3.launcher.model.GameConfig

fun GameConfigJson.toConfig(): GameConfig {
    return GameConfig(
        textLang = G3Language.fromKey(textLang),
        voiceLang = G3Language.fromKey(voiceLang),
        subs = subs,
        ruIntro = ruIntro,
    )
}

fun GameConfig.toJson(): GameConfigJson {
    return GameConfigJson(
        textLang = textLang.key,
        voiceLang = voiceLang.key,
        subs = subs,
        ruIntro = ruIntro,
    )
}
