package com.g3.launcher.manager

import java.io.File

/**
 * Работа с пакетами установки
 */
object PackagesManager {
    private const val BASE_PATH: String = "app/resources/base.zip"
    private const val EN_PATH: String = "app/resources/en.zip"
    private const val DE_PATH: String = "app/resources/de.zip"
    private const val FR_PATH: String = "app/resources/fr.zip"
    private const val PL_PATH: String = "app/resources/pl.zip"
    private const val RU_PATH: String = "app/resources/ru.zip"

    fun getAvailablePackages(): List<String> {
        val list = mutableListOf<String>()

        if (File(BASE_PATH).exists()) {
            list.add("base")
        }

        if (File(EN_PATH).exists()) {
            list.add("en")
        }

        if (File(DE_PATH).exists()) {
            list.add("de")
        }

        if (File(FR_PATH).exists()) {
            list.add("fr")
        }

        if (File(PL_PATH).exists()) {
            list.add("pl")
        }

        if (File(RU_PATH).exists()) {
            list.add("ru")
        }

        return list
    }
}
