package com.g3.launcher.manager

import java.io.File

object IniFileManager {

    /**
     * Обновляет значение в INI файле
     */
    fun updateValue(filePath: String, section: String, key: String, newValue: String): Boolean {
        return try {
            val file = File(filePath)
            if (!file.exists()) {
                return false
            }

            val lines = file.readLines()
            val updatedLines = mutableListOf<String>()
            var inTargetSection = false
            var keyUpdated = false

            for (line in lines) {
                val trimmedLine = line.trim()

                // Проверяем секцию
                if (trimmedLine.startsWith("[") && trimmedLine.endsWith("]")) {
                    // Если мы были в целевой секции и ключ не был обновлен, добавляем его
                    if (inTargetSection && !keyUpdated) {
                        updatedLines.add("$key=$newValue")
                        keyUpdated = true
                    }

                    inTargetSection = trimmedLine == "[$section]"
                    updatedLines.add(line)
                    continue
                }

                // Если мы в целевой секции и нашли нужный ключ
                if (inTargetSection && !keyUpdated && trimmedLine.startsWith("$key=")) {
                    updatedLines.add("$key=$newValue")
                    keyUpdated = true
                } else {
                    updatedLines.add(line)
                }
            }

            // Если мы все еще в целевой секции и ключ не был обновлен, добавляем в конец
            if (inTargetSection && !keyUpdated) {
                updatedLines.add("$key=$newValue")
            }

            file.writeText(updatedLines.joinToString("\n"))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    /**
     * Читает значение из INI файла
     */
    fun readValue(filePath: String, section: String, key: String): String? {
        return try {
            val file = File(filePath)
            if (!file.exists()) {
                return null
            }

            val lines = file.readLines()
            var inTargetSection = false

            for (line in lines) {
                val trimmedLine = line.trim()

                // Проверяем секцию
                if (trimmedLine.startsWith("[") && trimmedLine.endsWith("]")) {
                    inTargetSection = trimmedLine == "[$section]"
                    continue
                }

                // Если мы в целевой секции и нашли нужный ключ
                if (inTargetSection && trimmedLine.startsWith("$key=")) {
                    return trimmedLine.substringAfter("=").trim()
                }
            }

            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}