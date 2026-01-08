package com.g3.launcher.localization

interface Localization {
    val setupAction: String
    val setupDescription: String

    val nextAction: String

    val gameDirectory: String
    val savesDirectory: String
    val selectDirectory: String
    val fileNotFound: String

    val installation: String
    val file: String
    val removingOutdatedFiles: String
    val backupSaves: String
    val installationError: String
    val patchesLoading: String
    val localizationFilesLoading: String
    val patchesInstallation: String
    val localizationInstallation: String
    val patchedGameFilesLoading: String
    val cleanupObsoleteFiles: String
    val archiveReassembly: String
    val voiceFilesCopying: String
    val saveBackup: String
    val localizationFilesLoadingNote: String

    val play: String
    val playMods: String
    val options: String

    val language: String
    val graphics: String
    val game: String
    val general: String

    val textLanguage: String
    val voiceLanguage: String
    val additional: String
    val gothicFonts: String
    val fontDescription: String

    val subtitles: String
    val showSubtitlesInDialogs: String
    val localizedIntro: String
    val introDescription: String

    val main: String
    val configure: String
    val detailLevel: String
    val verticalSync: String
    val fpsLimit60: String

    val default: String
    val low: String
    val medium: String
    val high: String
    val veryHigh: String
    val custom: String

    val verticalSyncDescription: String
    val fpsLimit60Description: String

    val drawDistance: String

    val additionalLocalizationPackage: String

    val download: String
    val resume: String

    val copyDirectory: String
    val copyCurrentGameDirectory: String
    val playWithMods: String
    val modCopyDescription: String
    val deleteModDirectory: String
    val deleteModWarning: String
    val mainDirectories: String
    val moddedGameDirectory: String
    val gameSettingsFiles: String
    val deleteConfirmation: String
    val deleteModDirectoryConfirmation: String
    val failedToDeleteModDirectory: String
    val error: String
    val developerConsole: String
    val quickLoot: String
    val lockpickingMinigame: String
    val alternativeCamera: String
    val balance: String
    val alternativeBalance: String
    val questExperienceMultiplier: String
    val mobAttackDelay: String
    val openingCutscenes: String
    val moddedConfigurationToggle: String
    val moddedConfigurationDescription: String
    val developerConsoleDescription: String
    val quickLootDescription: String
    val lockpickingMinigameDescription: String
    val alternativeCameraDescription: String
    val alternativeBalanceDescription: String
    val questExperienceMultiplierDescription: String
    val combatExperienceMultiplier: String
    val combatExperienceMultiplierDescription: String
    val disableOpeningCutscenes: String
    val mobAttackDelayDescription: String
    val resetMultipliers: String
    val resetMultipliersDescription: String
    val extendedContent: String
    val restoredContentDescription: String
}