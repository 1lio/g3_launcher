package com.g3.launcher.ui.pane.install

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.g3.launcher.manager.LauncherManager
import com.g3.launcher.manager.PackagesManager
import com.g3.launcher.mapper.toLocales
import com.g3.launcher.model.InstallPackages
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InstallViewModel {
    sealed interface Stage {
        object Welcome : Stage

        object PackSelect : Stage

        class SelectDirs(
            val gameDir: String? = LauncherManager.config.gameDirPath,
            val gameDirError: String? = null,
            val saveDir: String? = LauncherManager.config.gameSaveDirPath,
            val saveDirErrorPath: String? = null,
        ) : Stage

        class Setup(
            val step: Int,
            val num: Int = 1,
            val count: Int = 64,
            val closeEnabled: Boolean = true
        ) : Stage

        class Error(
            val message: String
        ) : Stage
    }

    // Состояния загрузок
    data class DownloadState(
        val isBaseDownloading: Boolean = false,
        val baseComplete: Boolean = false,
        val languageDownloads: Map<String, Boolean> = emptyMap(), // язык -> завершено
        val activeLanguageDownloads: Set<String> = emptySet() // текущие загрузки
    )

    private val _downloadState = MutableStateFlow(DownloadState())
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    var stage: Stage by mutableStateOf(Stage.Welcome)
        private set

    var baseProgress: Int by mutableIntStateOf(0)
        private set

    var enProgress: Int by mutableIntStateOf(0)
        private set

    var deProgress: Int by mutableIntStateOf(0)
        private set

    var frProgress: Int by mutableIntStateOf(0)
        private set

    var plProgress: Int by mutableIntStateOf(0)
        private set

    var ruProgress: Int by mutableIntStateOf(0)
        private set

    private var baseDownloadJob: Job? = null
    private val languageDownloadJobs = mutableMapOf<String, Job>()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun runInstall() {
        stage = Stage.PackSelect

        startBaseDownload()
    }

    private fun startBaseDownload() {
        baseDownloadJob?.cancel()

        baseDownloadJob = scope.launch {
            _downloadState.value = _downloadState.value.copy(
                isBaseDownloading = true,
                baseComplete = false
            )

            try {
                PackagesManager.installBasePackage { progress ->
                    baseProgress = progress
                    println("Base: $progress%")
                }

                _downloadState.value = _downloadState.value.copy(
                    isBaseDownloading = false,
                    baseComplete = true
                )
                println("Base download completed")

            } catch (e: Exception) {
                println("Base download failed: ${e.message}")
                _downloadState.value = _downloadState.value.copy(
                    isBaseDownloading = false
                )
            }
        }
    }

    fun installPackages(installPackage: InstallPackages) {
        val locales = installPackage.toLocales()
        println("Starting language downloads: $locales")

        // Используйте coroutineScope для параллельного выполнения
        scope.launch {
            val results = PackagesManager.installMultipleLanguages(locales) { language, progress ->
                when (language) {
                    "de" -> deProgress = progress
                    "en" -> enProgress = progress
                    "fr" -> frProgress = progress
                    "pl" -> plProgress = progress
                    "ru" -> ruProgress = progress
                }
                println("$language : $progress%")

                // Обновляем состояние загрузки
                if (progress == 100) {
                    _downloadState.value = _downloadState.value.copy(
                        activeLanguageDownloads = _downloadState.value.activeLanguageDownloads - language,
                        languageDownloads = _downloadState.value.languageDownloads + (language to true)
                    )
                } else {
                    _downloadState.value = _downloadState.value.copy(
                        activeLanguageDownloads = _downloadState.value.activeLanguageDownloads + language
                    )
                }
            }

            // Все загрузки завершены
            println("Все языковые пакеты загружены: $results")
        }
    }
}
