package com.example.pulsefit.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

/**
 * Handles app-wide language selection.
 *
 * Languages supported:
 *   en  → English
 *   zu  → isiZulu
 *   af  → Afrikaans
 *   tn  → Setswana
 */
object LocaleHelper {

    private const val PREFS = "pulsefit_prefs"
    private const val KEY_LANG = "app_language"

    data class LanguageOption(val code: String, val displayName: String, val nativeName: String)

    val supportedLanguages = listOf(
        LanguageOption("en", "English",  "English"),
        LanguageOption("zu", "isiZulu",  "isiZulu"),
        LanguageOption("af", "Afrikaans","Afrikaans"),
        LanguageOption("tn", "Setswana", "Setswana")
    )

    /** Read the saved language code, defaulting to "en". */
    fun getSavedLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LANG, "en") ?: "en"
    }

    /** Persist a new language code. */
    fun setLanguage(context: Context, langCode: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANG, langCode)
            .apply()
    }

    /** Wrap a base context so its resources use the chosen locale. */
    fun wrap(base: Context): Context {
        val langCode = getSavedLanguage(base)
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            base.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            base.resources.updateConfiguration(config, base.resources.displayMetrics)
            base
        }
    }
}