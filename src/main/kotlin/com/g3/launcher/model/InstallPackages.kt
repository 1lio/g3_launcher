package com.g3.launcher.model

import androidx.compose.runtime.Immutable

@Immutable
data class InstallPackages(
    val en: Boolean = true,
    val de: Boolean = false,
    val fr: Boolean = false,
    val pl: Boolean = false,
    val ru: Boolean = false,
)
