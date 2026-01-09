package com.g3.launcher.ui.pane.option.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.g3.launcher.manager.GameManager
import com.g3.launcher.manager.GameSaveManager

class GameOptionViewModel {

    var testMode: Boolean by mutableStateOf(!GameManager.isTestMode())
        private set

    var quickLoot: Boolean by mutableStateOf(!GameManager.isQuickLoot())
        private set

    var lockGame: Boolean by mutableStateOf(!GameManager.isLockGame())
        private set

    var extendedContent: Boolean by mutableStateOf(!GameManager.isExtendedContent())
        private set

    var skipIntro: Boolean by mutableStateOf(!GameManager.isSkipIntro())
        private set

    var gothicFont: Boolean by mutableStateOf(GameManager.isGFont())
        private set

    var altCamera: Boolean by mutableStateOf(GameSaveManager.isAltCamera())
        private set

    var altAI: Boolean by mutableStateOf(GameSaveManager.isAltAI())
        private set

    var combatExp: Int by mutableStateOf(GameManager.getCombatExp())
        private set

    var questExp: Int by mutableStateOf(GameManager.getQuestExp())
        private set

    var attackDuration: Int by mutableIntStateOf(GameManager.getHintAttackDuration())
        private set

    fun updateSettingsType(mods: Boolean) {
        GameManager.setGameMode(mods)
        GameSaveManager.setGameMode(mods)

        skipIntro = !GameManager.isSkipIntro()
        gothicFont = GameManager.isGFont()
        testMode = GameManager.isTestMode()
        quickLoot = GameManager.isQuickLoot()
        lockGame = GameManager.isLockGame()
        extendedContent = GameManager.isExtendedContent()
        altCamera = GameSaveManager.isAltCamera()
        altAI = GameSaveManager.isAltAI()
        attackDuration = GameManager.getHintAttackDuration()
        combatExp = GameManager.getCombatExp()
        questExp = GameManager.getQuestExp()
    }

    fun testMode(value: Boolean) {
        GameManager.setTestMode(value)
        testMode = value
    }

    fun quickLoot(value: Boolean) {
        GameManager.setQuickLoot(value)
        quickLoot = value
    }

    fun enableExtendedContent(value: Boolean) {
        GameManager.setExtendedContent(value)
        GameSaveManager.setExtendedContent(value)
        extendedContent = value
    }

    fun lockGame(value: Boolean) {
        GameManager.setLockGame(value)
        lockGame = value
    }

    fun skipIntro(value: Boolean) {
        GameManager.skipIntro(!value)
        skipIntro = value
    }

    fun gFont(value: Boolean) {
        GameManager.setGFont(value)
        gothicFont = value
    }

    fun enableAltCamera(value: Boolean) {
        GameSaveManager.setAltCamera(value)
        altCamera = value
    }

    fun enableAltAI(value: Boolean) {
        GameSaveManager.setAltAI(value)
        altAI = value
    }

    fun setCombatExp(value: String) {
        if (value.isEmpty()) return

        var result = value
            .replace(Regex("[^0-9]"), "")
            .toInt()

        if (result > 200) {
            result = 200
        }

        if (result < 1) {
            result = 1
        }

        GameManager.setCombatExp(result)
        combatExp = result
    }

    fun setQuestExp(value: String) {
        if (value.isEmpty()) return

        var result = value
            .replace(Regex("[^0-9]"), "")
            .toInt()

        if (result > 200) {
            result = 200
        }

        if (result < 1) {
            result = 1
        }

        GameManager.setQuestExp(result)
        questExp = result
    }

    fun setAttackDuration(value: String) {
        if (value.isEmpty()) return

        var result = value
            .replace(Regex("[^0-9]"), "")
            .toInt()

        if (result > 15) {
            result = 15
        }

        if (result < 1) {
            result = 1
        }

        GameManager.setHintAttackDuration(result)
        attackDuration = result
    }

    fun resetBalance() {
        setAttackDuration("6")
        setCombatExp("100")
        setQuestExp("100")
    }
}
