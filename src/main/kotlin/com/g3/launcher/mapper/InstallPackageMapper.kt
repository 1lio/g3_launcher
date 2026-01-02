package com.g3.launcher.mapper

import com.g3.launcher.model.InstallPackages

fun InstallPackages.toLocales(): List<String> {
    return buildList {
        if (en) add("en")
        if (de) add("de")
        if (fr) add("fr")
        if (pl) add("pl")
        if (ru) add("ru")
    }
}
