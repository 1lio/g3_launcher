package com.g3.launcher.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.g3.launcher.g3_laucher.generated.resources.NotoSerifJP_Bold
import com.g3.launcher.g3_laucher.generated.resources.NotoSerifJP_Medium
import com.g3.launcher.g3_laucher.generated.resources.NotoSerifJP_Regular
import com.g3.launcher.g3_laucher.generated.resources.Res
import org.jetbrains.compose.resources.Font

object Fonts {
    @Composable
    fun appFont() = FontFamily(
        Font(
            Res.font.NotoSerifJP_Regular,
            FontWeight.Normal,
            FontStyle.Normal
        ),
        Font(
            Res.font.NotoSerifJP_Bold,
            FontWeight.Bold,
            FontStyle.Normal
        ),
        Font(
            Res.font.NotoSerifJP_Medium,
            FontWeight.Medium,
            FontStyle.Normal
        ),
    )
}
