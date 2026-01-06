package com.g3.launcher.ui.pane.option.language

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g3.launcher.g3_laucher.generated.resources.Res
import com.g3.launcher.g3_laucher.generated.resources.ic_ok
import com.g3.launcher.model.G3Language
import com.g3.launcher.model.LocalLanguage
import com.g3.launcher.ui.component.G3Text
import com.g3.launcher.ui.component.option.OptionGroup
import com.g3.launcher.ui.extension.hoverEffect
import com.g3.launcher.ui.theme.ColorOrange
import com.g3.launcher.ui.theme.ColorText
import com.g3.launcher.ui.theme.ColorTextSecondary
import org.jetbrains.compose.resources.painterResource

@Composable
fun OptionLanguagePane(
    viewModel: LanguageOptionViewModel = remember { LanguageOptionViewModel() }
) {
    val strings = LocalLanguage.current.strings

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        LanguageSelector(
            title = strings.textLanguage,
            languages = G3Language.entries,
            selectedLanguage = viewModel.currentTextLang,
            onLanguageSelected = viewModel::selectTextLang
        )

        Spacer(Modifier.height(20.dp))

        LanguageSelector(
            title = strings.voiceLanguage,
            languages = viewModel.voiceLanguages.mapNotNull {
                if (it.progress == 100) {
                    G3Language.fromKey(it.key)
                } else {
                    null
                }
            },
            selectedLanguage = viewModel.currentVoiceLang,
            onLanguageSelected = viewModel::selectVoiceLang
        )

        Spacer(Modifier.height(20.dp))

        OptionCheckBox(
            text = strings.subtitles,
            description = strings.showSubtitlesInDialogs,
            checked = viewModel.showSubs,
            onCheckedChange = viewModel::selectSubs
        )

        if (viewModel.optionRuIntro) {
            Spacer(Modifier.height(20.dp))
            OptionCheckBox(
                text = strings.localizedIntro,
                description = strings.introDescription,
                checked = viewModel.showRuInto,
                onCheckedChange = viewModel::selectRuIntro
            )
        }

        Spacer(Modifier.height(48.dp))

        if (viewModel.voiceLanguages.isNotEmpty()) {
            OptionGroup(
                text = strings.additionalLocalizationPackage,
                fontSize = 14.sp,
                color = ColorTextSecondary,
                fontWeight = FontWeight.Medium,
                itemSpacing = 20.dp
            ) {
                viewModel.voiceLanguages.forEach { lang ->
                    key(lang.key) {
                        DownloadOptionItem(
                            title = lang.title,
                            started = lang.started,
                            progress = lang.progress
                        ) { startDownload ->
                            viewModel.downloadLang(lang.key, startDownload)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageSelector(
    title: String,
    languages: List<G3Language>,
    selectedLanguage: G3Language,
    onLanguageSelected: (G3Language) -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

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
                text = selectedLanguage.title,
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
                    languages.forEach { language ->
                        G3Text(
                            text = language.title,
                            color = if (language.title == selectedLanguage.title) ColorOrange else Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clickable(null, null) {
                                    onLanguageSelected(language)
                                    expanded = false
                                }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OptionCheckBox(
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

@Composable
private fun DownloadOptionItem(
    title: String,
    started: Boolean,
    progress: Int,
    onClick: (Boolean) -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    val strings = LocalLanguage.current.strings

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .hoverEffect { isHovered = it }
            .clickable {
                when {
                    progress in 1..99 && started -> onClick(false) // пауза
                    progress in 1..99 && !started -> onClick(true) // продолжить
                    progress == 0 -> onClick(true)                 // старт
                }
            }
    ) {
        G3Text(
            text = title,
            color = ColorText,
            fontSize = 18.sp,
            hoverable = true,
            hoverForce = isHovered
        )

        if (progress == 100) {
            Image(
                painter = painterResource(Res.drawable.ic_ok),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        } else {

            G3Text(
                text = when {
                    started && progress < 100 -> "$progress%"
                    progress in 1..99 && !started -> strings.resume
                    else -> strings.download
                },
                color = ColorText,
                fontSize = 18.sp
            )
        }
    }
}
