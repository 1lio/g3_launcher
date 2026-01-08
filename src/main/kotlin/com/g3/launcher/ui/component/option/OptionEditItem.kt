package com.g3.launcher.ui.component.option

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g3.launcher.ui.component.G3Text
import com.g3.launcher.ui.theme.ColorOrange
import com.g3.launcher.ui.theme.ColorText
import com.g3.launcher.ui.theme.ColorTextGray
import com.g3.launcher.ui.theme.Fonts

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OptionEditItem(
    text: String,
    description: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    val color = Color(0xFFFCFCFC)
    val tooltipShape = RoundedCornerShape(8.dp)
    var currentValue by remember { mutableStateOf(value) }

    // Создаем interactionSource для отслеживания состояния фокуса
    val interactionSource = remember { MutableInteractionSource() }

    // Получаем текущее состояние фокуса
    val isFocused by interactionSource.collectIsFocusedAsState()

    val style = LocalTextStyle.current.copy(
        fontFamily = Fonts.appFont(),
        textAlign = TextAlign.Center,
        color = ColorText,
        fontSize = 18.sp,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TooltipArea(
            tooltip = {
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .width(300.dp)
                        .background(Color.Black, tooltipShape)
                        .border(1.dp, ColorOrange, tooltipShape)
                        .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 8.dp)
                ) {
                    G3Text(
                        text = description,
                        textAlign = TextAlign.Start,
                        color = ColorText,
                        fontSize = 12.sp
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.CenterStart)
        ) {
            G3Text(
                text = text,
                textAlign = TextAlign.Start,
                color = color,
                fontSize = 18.sp,
                hoverable = true,
                hoverForce = isFocused,
                modifier = Modifier
                    .wrapContentHeight()
                    .width(320.dp)
                    .padding(top = 4.dp)
            )
        }

        BasicTextField(
            value = currentValue,
            onValueChange = {
                currentValue = it
                onValueChange(it)
            },
            textStyle = style,
            singleLine = true,
            cursorBrush = SolidColor(ColorOrange),
            interactionSource = interactionSource,
            modifier = Modifier
                .width(60.dp)
                .wrapContentHeight()
                .align(Alignment.CenterEnd)
                .background(Color.Transparent)
        ) { innerTextField ->
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                innerTextField()
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(if (isFocused) ColorOrange else ColorTextGray)
                )
            }
        }
    }
}