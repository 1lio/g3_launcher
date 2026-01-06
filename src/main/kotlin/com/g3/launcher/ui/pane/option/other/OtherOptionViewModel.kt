package com.g3.launcher.ui.pane.option.other

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.g3.launcher.manager.LauncherManager
import com.g3.launcher.model.LauncherConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.io.File
import javax.swing.JOptionPane

class OtherOptionViewModel {

    private val config: LauncherConfig = LauncherManager.config
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var copyProgress by mutableStateOf(if (config.mods) 100 else 0)
        private set

    var isCopying by mutableStateOf(false)
        private set

    val gameDir = config.gameDirPath ?: ""
    val gameSaveDir = config.gameSaveDirPath ?: ""
    val gameWithModsDir = config.gameDirPath?.let { "$it\\GameWithMods" } ?: ""

    val ge3IniPath = "$gameDir\\Ini\\ge3.ini"
    val mountlistIniPath = "$gameDir\\Ini\\mountlist.ini"
    val userOptionPath = "$gameSaveDir\\UserOptions.ini"

    fun playWithMods(value: Boolean) {
        val modsDir = File(gameWithModsDir)
        val gameDirFile = File(gameDir)

        if (value) {
            if (!modsDir.exists() && gameDirFile.exists() && gameDirFile.isDirectory) {
                scope.launch {
                    try {
                        isCopying = true
                        copyProgress = 0

                        // Создаем директорию для модов
                        modsDir.mkdirs()

                        // Получаем список файлов для копирования
                        val filesToCopy = gameDirFile.listFiles()
                            ?.filter { it != gameDirFile && it.name != "GameWithMods" }
                            ?.takeIf { it.isNotEmpty() }
                            ?: emptyList()

                        if (filesToCopy.isEmpty()) {
                            copyProgress = 100
                            LauncherManager.updateConfig { copy(mods = true) }
                            isCopying = false
                            return@launch
                        }

                        // Считаем общее количество файлов для точного расчета прогресса
                        var totalFilesCount = 0
                        filesToCopy.forEach { file ->
                            if (file.isFile) {
                                totalFilesCount++
                            } else if (file.isDirectory) {
                                totalFilesCount += countFilesInDirectory(file)
                            }
                        }

                        if (totalFilesCount == 0) {
                            copyProgress = 100
                            LauncherManager.updateConfig { copy(mods = true) }
                            isCopying = false
                            return@launch
                        }

                        var copiedFilesCount = 0

                        filesToCopy.forEach { source ->
                            val target = File(modsDir, source.name)

                            if (source.isDirectory) {
                                // Копируем директорию рекурсивно с учетом прогресса
                                copyDirectoryWithProgress(source, target) { filesCopiedInDir ->
                                    copiedFilesCount += filesCopiedInDir
                                    val progress = (copiedFilesCount * 100) / totalFilesCount
                                    copyProgress = progress.coerceAtMost(100)
                                }
                            } else {
                                // Копируем файл
                                source.copyTo(target, overwrite = false)
                                copiedFilesCount++
                                val progress = (copiedFilesCount * 100) / totalFilesCount
                                copyProgress = progress
                            }
                        }

                        copyProgress = 100
                        LauncherManager.updateConfig { copy(mods = true) }

                    } catch (ex: Exception) {
                        JOptionPane.showMessageDialog(
                            null,
                            "Не удалось создать копию игры для модов:\n${ex.message}",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE
                        )
                        // Откатываем изменения при ошибке
                        modsDir.deleteRecursively()
                        copyProgress = 0
                    } finally {
                        isCopying = false
                    }
                }
            } else {
                LauncherManager.updateConfig { copy(mods = true) }
                copyProgress = 100
            }
        } else {
            LauncherManager.updateConfig { copy(mods = false) }
        }
    }

    private fun countFilesInDirectory(directory: File): Int {
        if (!directory.exists() || !directory.isDirectory) return 0

        var count = 0
        directory.walkTopDown().forEach {
            if (it.isFile) {
                count++
            }
        }
        return count
    }

    private suspend fun copyDirectoryWithProgress(
        source: File,
        target: File,
        onProgress: (Int) -> Unit
    ) {
        if (!source.exists() || !source.isDirectory) return

        target.mkdirs()

        val files = source.walkTopDown().toList()
        var filesCopied = 0

        files.forEach { file ->
            if (file == source) return@forEach // Пропускаем корневую директорию

            val relativePath = source.toPath().relativize(file.toPath())
            val targetFile = target.toPath().resolve(relativePath).toFile()

            if (file.isDirectory) {
                targetFile.mkdirs()
            } else {
                file.copyTo(targetFile, overwrite = false)
                filesCopied++
                onProgress(1) // Сообщаем о каждом скопированном файле
            }
        }
    }

    fun removeModsDir() {
        val modsDir = File(gameWithModsDir)
        val gameDirFile = File(gameDir)

        if (isCopying) {
            JOptionPane.showMessageDialog(
                null,
                "Подождите завершения копирования",
                "Внимание",
                JOptionPane.WARNING_MESSAGE
            )
            return
        }

        if (modsDir.exists() && modsDir.isDirectory &&
            modsDir.absolutePath.startsWith(gameDirFile.absolutePath)
        ) {
            val confirm = JOptionPane.showConfirmDialog(
                null,
                "Вы уверены, что хотите удалить директорию модов?\n${modsDir.absolutePath}",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            )

            if (confirm == JOptionPane.YES_OPTION) {
                scope.launch {
                    try {
                        modsDir.deleteRecursively()
                        LauncherManager.updateConfig { copy(mods = false) }
                    } catch (ex: Exception) {
                        JOptionPane.showMessageDialog(
                            null,
                            "Не удалось удалить директорию модов:\n${ex.message}",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE
                        )
                    }
                }
            }
        }
    }

    fun openGameDir() {
        openDirectory(gameDir)
    }

    fun openGameWithModsDir() {
        openDirectory(gameWithModsDir)
    }

    fun openSaveDir() {
        openDirectory(gameSaveDir)
    }

    fun openGe3() {
        openFileInNotepad(ge3IniPath)
    }

    fun openMountList() {
        openFileInNotepad(mountlistIniPath)
    }

    fun openUserOptions() {
        openFileInNotepad(userOptionPath)
    }

    private fun openDirectory(path: String) {
        if (path.isBlank()) {
            showError("Путь не указан")
            return
        }

        val directory = File(path)
        if (!directory.exists()) {
            showError("Директория не найдена:\n$path")
            return
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(directory)
            } else {
                openWithSystemCommand(directory)
            }
        } catch (ex: Exception) {
            showError("Не удалось открыть директорию:\n${ex.message}")
        }
    }

    private fun openFileInNotepad(filePath: String) {
        if (filePath.isBlank()) {
            showError("Путь к файлу не указан")
            return
        }

        val file = File(filePath)
        if (!file.exists()) {
            showError("Файл не найден:\n$filePath")
            return
        }

        if (!file.isFile) {
            showError("Указанный путь не является файлом:\n$filePath")
            return
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file)
            } else {
                // Альтернативный способ для систем без Desktop API
                openFileWithSystemEditor(file)
            }
        } catch (ex: Exception) {
            showError("Не удалось открыть файл в блокноте:\n${ex.message}")
        }
    }

    private fun openWithSystemCommand(directory: File) {
        try {
            Runtime.getRuntime().exec(arrayOf("explorer", directory.absolutePath))
        } catch (ex: Exception) {
            throw RuntimeException("Failed to open directory with system command", ex)
        }
    }

    private fun openFileWithSystemEditor(file: File) {
        try {
            Runtime.getRuntime().exec(arrayOf("notepad", file.absolutePath))
        } catch (ex: Exception) {
            throw RuntimeException("Failed to open file with system editor", ex)
        }
    }

    private fun showError(message: String) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Ошибка",
            JOptionPane.ERROR_MESSAGE
        )
    }
}