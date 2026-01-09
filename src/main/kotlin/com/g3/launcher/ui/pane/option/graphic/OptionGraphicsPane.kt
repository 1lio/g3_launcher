package com.g3.launcher.ui.pane.option.graphic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g3.launcher.model.G3DistancePreset
import com.g3.launcher.model.G3GraphicPreset
import com.g3.launcher.model.G3DisplayMode
import com.g3.launcher.model.LocalConfig
import com.g3.launcher.model.LocalLanguage
import com.g3.launcher.model.Preset
import com.g3.launcher.ui.component.G3Text
import com.g3.launcher.ui.component.option.OptionCheckBox
import com.g3.launcher.ui.component.option.OptionGroup
import com.g3.launcher.ui.extension.hoverEffect
import com.g3.launcher.ui.theme.ColorOrange
import com.g3.launcher.ui.theme.ColorText
import com.g3.launcher.ui.theme.ColorTextSecondary
import kotlin.collections.forEach

@Composable
fun OptionGraphicsPane(
    viewModel: GraphicsOptionViewModel = remember { GraphicsOptionViewModel() }
) {
    val config = LocalConfig.current
    val strings = LocalLanguage.current.strings

    LaunchedEffect(config) {
        viewModel.updateSettingsType(config.modsConfig)
    }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            LanguageSelector(
                title = strings.displayMode,
                items = G3DisplayMode.entries,
                selected = viewModel.currentDisplayMode,
                onSelected = viewModel::setDisplayMode
            )
            LanguageSelector(
                title = strings.detailLevel,
                items = G3GraphicPreset.entries,
                selected = viewModel.currentGraphicsPreset,
                onSelected = viewModel::setGraphicsPreset
            )

            LanguageSelector(
                title = strings.drawDistance,
                items = G3DistancePreset.entries,
                selected = viewModel.currentDistancePreset,
                onSelected =viewModel::setDistancePreset
            )
        }

        Spacer(Modifier.height(40.dp))

        OptionGroup(
            text = strings.additional,
            fontSize = 14.sp,
            color = ColorTextSecondary,
            fontWeight = FontWeight.Medium,
        ) {
            OptionCheckBox(
                text = strings.verticalSync,
                description = strings.verticalSyncDescription,
                checked = viewModel.vSync,
                onCheckedChange = viewModel::setVsync
            )

            OptionCheckBox(
                text = strings.fpsLimit60,
                description = strings.fpsLimit60Description,
                checked = viewModel.fpsLimit,
                onCheckedChange = viewModel::setFpsLimit
            )
        }
    }
}

@Composable
private fun <T : Preset> LanguageSelector(
    title: String,
    items: List<T>,
    selected: T,
    onSelected: (T) -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val strings = LocalLanguage.current.strings

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(null, null) {
                expanded = true
            }
    ) {
        G3Text(
            text = title,
            color = ColorText,
            hoverable = true,
            fontSize = 18.sp,
            hoverForce = isHovered,
            hoverSize = 12f,
        )

        Box(
            modifier = Modifier
                .hoverEffect {
                    isHovered = it
                }
                .clickable(null, null) {
                    expanded = true
                }
        ) {
            G3Text(
                text = when (selected.key) {
                    "low" -> strings.low
                    "medium" -> strings.medium
                    "high" -> strings.high
                    "veryHigh" -> strings.veryHigh
                    "default" -> strings.default
                    "windowed" -> strings.windowed
                    "borderlessWindow" -> strings.borderlessWindow
                    "fullscreen" -> strings.fullscreen
                    else -> ""
                },
                color = ColorText,
                hoverable = true,
                fontSize = 18.sp,
                hoverForce = isHovered,
                hoverSize = 14f,
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color.Black)
                    .border(1.dp, ColorOrange, RoundedCornerShape(8.dp))
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    items.forEach { item ->
                        G3Text(
                            text = when (item.key) {
                                    "low" -> strings.low
                                    "medium" -> strings.medium
                                    "high" -> strings.high
                                    "veryHigh" -> strings.veryHigh
                                    "default" -> strings.default
                                    "windowed" -> strings.windowed
                                    "borderlessWindow" -> strings.borderlessWindow
                                    "fullscreen" -> strings.fullscreen
                                    else -> ""
                                },
                            color = if (item.key == selected.key) ColorOrange else Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            hoverable = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clickable(null, null) {
                                    onSelected(item)
                                    expanded = false
                                }
                        )
                    }
                }
            }
        }
    }
}
