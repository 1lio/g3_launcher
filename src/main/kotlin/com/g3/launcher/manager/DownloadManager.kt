package com.g3.launcher.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object DownloadManager {
    private val scope = CoroutineScope(Dispatchers.IO)

    data class DownloadState(
        val isDownloading: Boolean = false,
        val fileName: String = "",
        val progress: Int = 0,
        val totalSize: Long? = null,
        val downloadedSize: Long = 0,
        val speed: Double = 0.0,
        val error: String? = null
    )

    private val _downloadState = MutableStateFlow(DownloadState())
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    fun downloadBasePackage() {
        scope.launch {
            _downloadState.value = DownloadState(
                isDownloading = true,
                fileName = "base.zip",
                progress = 0
            )

            try {
                PackagesManager.installBasePackage { progress ->
                    _downloadState.value = _downloadState.value.copy(progress = progress)
                }

                _downloadState.value = DownloadState(
                    isDownloading = false,
                    fileName = "base.zip",
                    progress = 100
                )

            } catch (e: Exception) {
                _downloadState.value = DownloadState(
                    isDownloading = false,
                    fileName = "base.zip",
                    error = e.message ?: "Неизвестная ошибка"
                )
            }
        }
    }

    fun cancelDownload() {
        _downloadState.value = DownloadState()
    }
}
