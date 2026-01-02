package com.g3.launcher.manager

import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.net.URI

object SteamManager {

    private const val GAME_ID: Int = 39500

    fun isSteamRunning(): Boolean {
        return try {
            val command = arrayOf("tasklist", "/FI", "IMAGENAME eq steam.exe")
            val process = Runtime.getRuntime().exec(command)
            val output = process.inputStream.bufferedReader().readText()

            output.contains("steam.exe")

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun startGame(): Boolean {
        return try {
            Desktop.getDesktop().browse(URI("steam://run/$GAME_ID"))
            true
        } catch (_: Exception) {
            false
        }
    }

    fun isGameStarted(): Boolean {
        return try {
            val path = "HKCU\\Software\\Valve\\Steam\\Apps\\$GAME_ID"
            val running = RegistryManager.getRegProperty(
                key = path,
                value = "Running"
            )

            val updating = RegistryManager.getRegProperty(
                key = path,
                value = "Updating"
            )

            running == "1" || updating == "1"
        } catch (_: Exception) {
            false
        }
    }

    fun isGameProcessRunning(): Boolean {
        return try {
            val process = ProcessBuilder("tasklist").start()
            val output = process.inputStream.bufferedReader().use { it.readText() }

            val gameProcessNames = listOf("Gothic3.exe")
            gameProcessNames.any { processName ->
                output.contains(processName, ignoreCase = true)
            }
        } catch (_: IOException) {
            false
        }
    }

    fun launchSteam(): Boolean {
        return try {
            // Сначала пробуем через steam:// URI
            if (launchSteamViaUri()) {
                println("Steam запущен через URI")
                return true
            }

            // Если не сработало, пробуем найти и запустить исполняемый файл
            launchSteamViaExecutable()
        } catch (e: Exception) {
            println("Ошибка при запуске Steam: ${e.message}")
            false
        }
    }

    /**
     * Запускает Steam через steam:// URI
     */
    private fun launchSteamViaUri(): Boolean {
        return try {
            Desktop.getDesktop().browse(URI("steam://"))
            true
        } catch (e: Exception) {
            println("Не удалось запустить Steam через URI: ${e.message}")
            false
        }
    }

    /**
     * Запускает Steam через исполняемый файл
     */
    private fun launchSteamViaExecutable(): Boolean {
        return try {
            val steamPath = getWindowsSteamPath()
            if (steamPath != null && File(steamPath).exists()) {
                val os = System.getProperty("os.name").lowercase()
                when {
                    os.contains("win") -> {
                        Runtime.getRuntime().exec(steamPath)
                        true
                    }

                    else -> false
                }
            } else {
                println("Steam не найден в системе")
                false
            }
        } catch (e: Exception) {
            println("Ошибка при запуске исполняемого файла Steam: ${e.message}")
            false
        }
    }


    private fun getWindowsSteamPath(): String? {
        return try {
            val process = Runtime.getRuntime().exec(
                arrayOf(
                    "reg", "query", "HKCU\\Software\\Valve\\Steam", "/v", "SteamExe"
                )
            )
            val output = process.inputStream.bufferedReader().readText()
            val regex = """REG_SZ\s+(.+)""".toRegex()
            regex.find(output)?.groups?.get(1)?.value?.trim()
        } catch (_: Exception) {
            val defaultPath = "C:\\Program Files (x86)\\Steam\\steam.exe"
            if (File(defaultPath).exists()) defaultPath else null
        }
    }

    /**
     * Проверяет, установлена ли конкретная игра локально
     */
    fun isGameInstalledLocally(): Boolean {
        return try {
            val steamPath = getWindowsSteamPath() ?: return false
            val manifestPath = File(steamPath).parent + "\\steamapps\\appmanifest_${GAME_ID}.acf"

            File(manifestPath).exists()
        } catch (_: Exception) {
            false
        }
    }
}