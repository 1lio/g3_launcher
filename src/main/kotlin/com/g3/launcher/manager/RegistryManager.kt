package com.g3.launcher.manager

import java.io.BufferedReader
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Paths

object RegistryManager {

    private const val JOWOOD_GOTHIC_KEY = "HKCU\\Software\\JoWooD\\Gothic III"
    private const val STEAM_GOTHIC_KEY = "HKCU\\SOFTWARE\\Valve\\Steam\\Apps\\39500"
    private const val DOCUMENTS = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders"
    private const val UNINSTALL_STEAM_GOTHIC_KEY = "HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 39500"

    fun getSystemLanguage(): String {
        val lang = getRegProperty(JOWOOD_GOTHIC_KEY, "OSLanguage")
        return lang ?: "en"
    }

    fun getGameDir(): String? {
        val jowood = getRegProperty(JOWOOD_GOTHIC_KEY, "INSTALL_DIR")?.normalizePath()
        if (!jowood.isNullOrBlank() && File(jowood).exists()) return jowood

        val steam = getRegProperty(STEAM_GOTHIC_KEY, "InstallLocation")?.normalizePath()
        if (!steam.isNullOrBlank() && File(steam).exists()) return steam

        val unstall = getRegProperty(UNINSTALL_STEAM_GOTHIC_KEY, "InstallLocation")?.normalizePath()
        if (!unstall.isNullOrBlank() && File(unstall).exists()) return unstall

        return null
    }

    fun getGameSaveDir(): String? {
        val userDocs = getRegProperty(DOCUMENTS, "Personal")?.normalizePath() ?: return null
        val saveDir = Paths.get(userDocs, "gothic3").toFile()
        if (saveDir.exists()) return saveDir.absolutePath

        return null
    }

    fun getRegProperty(key: String, value: String): String? {
        return try {
            val psCommand = """
            powershell -NoProfile -Command "[Console]::OutputEncoding = [System.Text.Encoding]::UTF8; (Get-ItemProperty -Path 'Registry::$key' -Name '$value').$value"
        """.trimIndent()

            val process = ProcessBuilder("cmd", "/c", psCommand)
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader(Charset.forName("UTF-8"))
                .use(BufferedReader::readText)
                .trim()

            process.waitFor()
            output.ifBlank { null }
        } catch (_: Exception) {
            null
        }
    }

    private fun String.normalizePath(): String = replace('/', '\\').trim('"', ' ')
}