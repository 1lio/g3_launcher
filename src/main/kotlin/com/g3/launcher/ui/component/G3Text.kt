package com.g3.launcher.ui.component

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.g3.launcher.ui.extension.hoverEffect
import com.g3.launcher.ui.theme.ColorOrange
import com.g3.launcher.ui.theme.ColorText
import com.g3.launcher.ui.theme.Fonts

@Composable
fun G3Text(
    text: String,
    enabled: Boolean = true,
    hoverable: Boolean = false,
    hoverForce: Boolean = false,
    hoverColor: Color = ColorOrange,
    color: Color = ColorText,
    textAlign: TextAlign = TextAlign.Start,
    fontWeight: FontWeight? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    shadow: Boolean = false,
    shadowColor: Color = Color.Black,
    textStyle: TextStyle? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    modifier: Modifier = Modifier,
) {
    var isEnabled by remember { mutableStateOf(enabled) }
    var isHovered by remember { mutableStateOf(false) }
    var isHover by remember { mutableStateOf(hoverable) }

    val style = if (shadow) {
        textStyle ?: LocalTextStyle.current.copy(
            fontFamily = Fonts.appFont(),
            shadow = Shadow(
                color = shadowColor,
                offset = Offset(0f, 0f),
                blurRadius = 2f
            )
        )
    } else {
        textStyle ?: LocalTextStyle.current
    }

    val hoverStyle = if (isHovered) {
        style.copy(
            shadow = Shadow(
                color = hoverColor,
                offset = Offset(0f, 0f),
                blurRadius = 8f
            )
        )
    } else {
        style
    }

    LaunchedEffect(enabled) {
        isEnabled = enabled
        if (!isEnabled) {
            isHovered = false
        }
    }

    LaunchedEffect(hoverForce) {
        if (isHover) {
            isHovered = hoverForce
        }
    }

    LaunchedEffect(hoverable) {
        isHover = hoverable

        if (!hoverable) {
            isHovered = false
        }
    }

    Text(
        text = text,
        textAlign = textAlign,
        fontFamily = Fonts.appFont(),
        fontWeight = fontWeight,
        color = color,
        fontSize = fontSize,
        style = textStyle ?: hoverStyle,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
            .hoverEffect {
                if (isHover && isEnabled) {
                    isHovered = it
                }
            }
    )
}

@Composable
fun G3Text(
    text: String,
    color: Color = ColorText,
    textAlign: TextAlign = TextAlign.Start,
    fontWeight: FontWeight? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    textStyle: TextStyle? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        textAlign = textAlign,
        fontFamily = Fonts.appFont(),
        fontWeight = fontWeight,
        color = color,
        fontSize = fontSize,
        style = textStyle ?: LocalTextStyle.current,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        maxLines = maxLines,
        modifier = modifier
    )
}