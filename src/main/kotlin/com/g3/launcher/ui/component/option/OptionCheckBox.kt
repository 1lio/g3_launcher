package com.g3.launcher.ui.component.option

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
fun OptionCheckBox(
    text: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val color = Color(0xFFFCFCFC)
    val shape = RoundedCornerShape(4.dp)
    val tooltipShape = RoundedCornerShape(8.dp)
    var isSelected by remember { mutableStateOf(checked) }
    var isHovered by remember { mutableStateOf(false) }
    val iconColorAnim by animateColorAsState(
        targetValue = if (isHovered) ColorOrange else Color.Black,
        label = "iconColor"
    )

    LaunchedEffect(checked) {
        isSelected = checked
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .hoverEffect {
                isHovered = it
            }
            .clickable(null, null) {
                onCheckedChange(!isSelected)
                isSelected = !isSelected
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

        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color.White, shape)
                .padding(2.dp)
                .background(Color.Black, shape)
                .padding(2.dp)
                .background(if (isSelected) Color.White else Color.Black, shape)
                .shadow(
                    elevation = 24.dp,
                    shape = CircleShape,
                    ambientColor = iconColorAnim,
                    spotColor = iconColorAnim,
                )
        )
    }
}
