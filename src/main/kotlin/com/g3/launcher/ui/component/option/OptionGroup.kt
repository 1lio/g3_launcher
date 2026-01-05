package com.g3.launcher.ui.component.option

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g3.launcher.ui.component.G3Text
import com.g3.launcher.ui.theme.ColorText

@Composable
fun OptionGroup(
    text: String,
    fontSize: TextUnit = 20.sp,
    color: Color = ColorText,
    fontWeight: FontWeight = FontWeight.Medium,
    spacing: Dp = 24.dp,
    itemSpacing: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        G3Text(
            text = text,
            textAlign = TextAlign.Start,
            fontWeight = fontWeight,
            color = color,
            fontSize = fontSize,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(itemSpacing, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            content(this)
        }
    }
}

