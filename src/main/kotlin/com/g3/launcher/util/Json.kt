package com.g3.launcher.util

import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    isLenient = false
    encodeDefaults = true
}
