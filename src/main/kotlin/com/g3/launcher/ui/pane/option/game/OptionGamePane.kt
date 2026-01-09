package com.g3.launcher.ui.pane.option.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g3.launcher.model.LocalConfig
import com.g3.launcher.model.LocalLanguage
import com.g3.launcher.ui.component.option.OptionCheckBox
import com.g3.launcher.ui.component.option.OptionEditItem
import com.g3.launcher.ui.component.option.OptionGroup
import com.g3.launcher.ui.component.option.OptionItem
import com.g3.launcher.ui.theme.ColorTextSecondary

@Composable
fun OptionGamePane(
    viewModel: GameOptionViewModel = remember { GameOptionViewModel() }
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
            OptionCheckBox(
                text = strings.developerConsole,
                description = strings.developerConsoleDescription,
                checked = viewModel.testMode,
                onCheckedChange = viewModel::testMode
            )

            OptionCheckBox(
                text = strings.quickLoot,
                description = strings.quickLootDescription,
                checked = viewModel.quickLoot,
                onCheckedChange = viewModel::quickLoot
            )

            OptionCheckBox(
                text = strings.lockpickingMinigame,
                description = strings.lockpickingMinigameDescription,
                checked = viewModel.lockGame,
                onCheckedChange = viewModel::lockGame
            )

            OptionCheckBox(
                text = strings.alternativeCamera,
                description = strings.alternativeCameraDescription,
                checked = viewModel.altCamera,
                onCheckedChange = viewModel::enableAltCamera
            )

            OptionCheckBox(
                text = strings.alternativeAI,
                description = strings.alternativeAIDescription,
                checked = viewModel.altAI,
                onCheckedChange = viewModel::enableAltAI
            )
        }

        Spacer(Modifier.height(40.dp))

        OptionGroup(
            text = strings.balance,
            fontSize = 14.sp,
            color = ColorTextSecondary,
            fontWeight = FontWeight.Medium,
            itemSpacing = 20.dp
        ) {
            if (!config.modsConfig) {
                OptionCheckBox(
                    text = strings.alternativeBalance,
                    description = strings.alternativeBalanceDescription + "\n\n" + strings.restoredContentDescription,
                    checked = viewModel.extendedContent,
                    onCheckedChange = viewModel::enableExtendedContent
                )
            }

            OptionEditItem(
                text = strings.questExperienceMultiplier,
                description = strings.questExperienceMultiplierDescription,
                value = "${viewModel.questExp}%",
                onValueChange = viewModel::setQuestExp,
            )

            OptionEditItem(
                text = strings.combatExperienceMultiplier,
                description = strings.combatExperienceMultiplierDescription,
                value = "${viewModel.combatExp}%",
                onValueChange = viewModel::setCombatExp,
            )

            OptionEditItem(
                text = strings.mobAttackDelay,
                description = strings.mobAttackDelayDescription,
                value = viewModel.attackDuration.toString(),
                onValueChange = viewModel::setAttackDuration,
            )

            OptionItem(
                text = strings.resetMultipliers,
                description = strings.resetMultipliersDescription,
                onClick = viewModel::resetBalance
            )
        }

        Spacer(Modifier.height(40.dp))

        OptionGroup(
            text = strings.additional,
            fontSize = 14.sp,
            color = ColorTextSecondary,
            fontWeight = FontWeight.Medium,
            itemSpacing = 20.dp
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                OptionCheckBox(
                    text = strings.openingCutscenes,
                    description = strings.disableOpeningCutscenes,
                    checked = viewModel.skipIntro,
                    onCheckedChange = viewModel::skipIntro
                )

                if (!config.modsConfig) {
                    OptionCheckBox(
                        text = strings.gothicFonts,
                        description = strings.fontDescription,
                        checked = viewModel.gothicFont,
                        onCheckedChange = viewModel::gFont
                    )
                }
            }
        }
    }
}
