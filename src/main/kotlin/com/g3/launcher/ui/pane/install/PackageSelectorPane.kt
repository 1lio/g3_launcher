package com.g3.launcher.ui.pane.install

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g3.launcher.g3_laucher.generated.resources.Res
import com.g3.launcher.g3_laucher.generated.resources.headline_line
import com.g3.launcher.model.G3Language
import com.g3.launcher.model.InstallPackages
import com.g3.launcher.model.LocalConfig
import com.g3.launcher.model.LocalLanguage
import com.g3.launcher.presentation.extension.hoverEffect
import com.g3.launcher.ui.component.G3Text
import com.g3.launcher.ui.theme.ColorOrange
import com.g3.launcher.ui.theme.ColorTextGray
import org.jetbrains.compose.resources.painterResource

@Composable
fun BoxScope.PackageSelectorPane(
    onClick: (InstallPackages) -> Unit
) {
    val strings = LocalLanguage.current.strings
    val config = LocalConfig.current

    var needDe by remember { mutableStateOf(config.language == G3Language.De) }
    var needFr by remember { mutableStateOf(config.language == G3Language.Fr) }
    var needPl by remember { mutableStateOf(config.language == G3Language.Pl) }
    var needRu by remember { mutableStateOf(config.language == G3Language.Ru) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(380.dp)
            .wrapContentHeight()
            .padding(24.dp)
            .align(alignment = Alignment.CenterEnd)
    ) {
        G3Text(
            text = strings.nextAction,
            hoverable = true,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            maxLines = 1,
            modifier = Modifier.clickable(
                interactionSource = null,
                indication = null,
                onClick = {
                    onClick(
                        InstallPackages(
                            de = needDe,
                            fr = needFr,
                            pl = needPl,
                            ru = needRu,
                        )
                    )
                },
            ),
        )

        Image(
            painter = painterResource(Res.drawable.headline_line),
            contentDescription = null,
            modifier = Modifier
                .height(14.dp)
                .fillMaxWidth()
        )

        G3Text(
            text = strings.additionalLocalizationPackage,
            color = ColorTextGray,
            fontSize = 15.sp,
            maxLines = 1,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            LocalizationBox(
                locale = G3Language.De,
                checked = needDe,
            ) {
                needDe = it
            }
            LocalizationBox(
                locale = G3Language.Fr,
                checked = needFr,
            ) {
                needFr = it
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth()
        ) {
            LocalizationBox(
                locale = G3Language.Pl,
                checked = needPl,
            ) {
                needPl = it
            }
            LocalizationBox(
                locale = G3Language.Ru,
                checked = needRu
            ) {
                needRu = it
            }
        }
    }
}

@Composable
private fun LocalizationBox(
    locale: G3Language,
    checked: Boolean = false,
    onClick: (Boolean) -> Unit = {},
) {
    var isHovered by remember { mutableStateOf(checked) }
    var isSelected by remember { mutableStateOf(checked) }
    val shape = RoundedCornerShape(4.dp)

    val iconColorAnim by animateColorAsState(
        targetValue = if (isHovered) ColorOrange else Color.Black,
        label = "iconColor"
    )

    Box(
        modifier = Modifier
            .wrapContentHeight()
            .width(124.dp)
            .hoverEffect {
                if (!isSelected) {
                    isHovered = it
                }
            }
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = {
                    isSelected = !isSelected
                    isHovered = true

                    onClick(isSelected)
                }
            )
    ) {
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
                .align(Alignment.TopStart)
        )


        val hoverStyle = if (isHovered) {
            LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = ColorOrange,
                    offset = Offset(0f, 0f),
                    blurRadius = 8f
                )
            )
        } else {
            LocalTextStyle.current
        }

        G3Text(
            text = locale.title,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            maxLines = 1,
            textStyle = hoverStyle,
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .wrapContentWidth()
                .padding(start = 36.dp)
        )
    }
}