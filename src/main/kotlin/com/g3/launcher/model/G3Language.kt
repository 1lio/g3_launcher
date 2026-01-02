package com.g3.launcher.model

import androidx.compose.runtime.Immutable
import com.g3.launcher.localization.CzLocalization
import com.g3.launcher.localization.DeLocalization
import com.g3.launcher.localization.EnLocalization
import com.g3.launcher.localization.EsLocalization
import com.g3.launcher.localization.FrLocalization
import com.g3.launcher.localization.HuLocalization
import com.g3.launcher.localization.ItLocalization
import com.g3.launcher.localization.Localization
import com.g3.launcher.localization.PlLocalization
import com.g3.launcher.localization.RuLocalization
import com.g3.launcher.localization.TrcLocalization

@Immutable
enum class G3Language(
    val key: String,
    val option: String,
    val title: String,
    val localizedVoice: Boolean,
    val strings: Localization,
) {
    En("en", "English", "English", true, EnLocalization()),
    It("it", "Italian", "Italiano", false, ItLocalization()),
    Fr("fr", "French", "Français", true, FrLocalization()),
    De("de", "German", "Deutsch", true, DeLocalization()),
    Es("es", "Spanish", "Español", true, EsLocalization()),
    Cz("cz", "Czech", "Čeština", false, CzLocalization()),
    Hu("hu", "Hungarian", "Magyar", false, HuLocalization()),
    Pl("pl", "Polish", "Polski", true, PlLocalization()),
    Ru("ru", "Russian", "Русский", true, RuLocalization()),
    TRC("trc", "TRC", "中國人", false, TrcLocalization());

    companion object {
        fun fromKey(key: String?): G3Language = entries.firstOrNull { it.key == key } ?: En
    }
}
