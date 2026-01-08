package com.g3.launcher.ui.component.option

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g3.launcher.ui.component.G3Text
import com.g3.launcher.ui.extension.hoverEffect
import com.g3.launcher.ui.theme.ColorOrange
import com.g3.launcher.ui.theme.ColorText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OptionItem(
    text: String,
    description: String,
    onClick: () -> Unit
) {
    val color = Color(0xFFFCFCFC)
    val tooltipShape = RoundedCornerShape(8.dp)
    var isHovered by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .hoverEffect {
                isHovered = it
            }
            .clickable(null, null) {
                onClick()
            }
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
        ) {
            G3Text(
                text = text,
                textAlign = TextAlign.Start,
                color = color,
                hoverable = true,
                hoverForce = isHovered,
                fontSize = 18.sp,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(bottom = 2.dp)
            )
        }
    }
}