package com.g3.launcher.ui.component.option

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g3.launcher.g3_laucher.generated.resources.Res
import com.g3.launcher.g3_laucher.generated.resources.headline_line
import com.g3.launcher.model.LocalLanguage
import com.g3.launcher.ui.component.G3Text
import com.g3.launcher.ui.theme.ColorOrange
import com.g3.launcher.ui.theme.ColorText
import org.jetbrains.compose.resources.painterResource

@Composable
fun OptionTabSelector(
    onSelect: (Int) -> Unit,
) {
    val strings = LocalLanguage.current.strings
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            TabItem(
                text = strings.language,
                selected = selectedTab == 0,
                onSelect = {
                    selectedTab = 0
                    onSelect(0)
                },
            )

            TabItem(
                text = strings.graphics,
                selected = selectedTab == 1,
                onSelect = {
                    selectedTab = 1
                    onSelect(1)
                },
            )

            TabItem(
                text = strings.game,
                selected = selectedTab == 2,
                onSelect = {
                    selectedTab = 2
                    onSelect(2)
                },
            )

            TabItem(
                text = strings.general,
                selected = selectedTab == 3,
                onSelect = {
                    selectedTab = 3
                    onSelect(3)
                },
            )
        }

        Spacer(Modifier.height(36.dp))

        Image(
            painter = painterResource(Res.drawable.headline_line),
            contentDescription = null,
            modifier = Modifier
                // .height(12.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun TabItem(
    text: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    if (selected) {
        G3Text(
            text = text,
            fontWeight = FontWeight.Bold,
            hoverable = false,
            fontSize = 24.sp,
            color = ColorOrange,
            /*textStyle = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = ColorOrange,
                    offset = Offset(0f, 0f),
                    blurRadius = 8f
                )
            ),*/
            modifier = Modifier
                .clickable(enabled = false, onClick = onSelect)
        )
    } else {
        G3Text(
            text = text,
            fontWeight = FontWeight.Bold,
            hoverable = true,
            fontSize = 24.sp,
            color = ColorText,
            modifier = Modifier
                .clickable(enabled = true, onClick = onSelect)

        )
    }
}
