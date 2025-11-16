package com.chanssem.freedive.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LanguageManager {
    private const val PREFS_NAME = "freedive_chanssem_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    
    enum class Language(val code: String, val locale: Locale) {
        KOREAN("ko", Locale.KOREAN),
        ENGLISH("en", Locale.ENGLISH),
        JAPANESE("ja", Locale.JAPANESE),
        CHINESE("zh", Locale.SIMPLIFIED_CHINESE);
        
        companion object {
            fun fromCode(code: String): Language {
                return values().find { it.code == code } ?: KOREAN
            }
        }
    }
    
    fun getSavedLanguage(context: Context): Language {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedCode = prefs.getString(KEY_LANGUAGE, null)
        return if (savedCode != null) {
            Language.fromCode(savedCode)
        } else {
            // 시스템 언어 감지
            detectSystemLanguage(context)
        }
    }
    
    fun saveLanguage(context: Context, language: Language) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
    }
    
    fun detectSystemLanguage(context: Context): Language {
        val systemLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
        
        return when (systemLocale.language) {
            "ko" -> Language.KOREAN
            "en" -> Language.ENGLISH
            "ja" -> Language.JAPANESE
            "zh" -> Language.CHINESE
            else -> Language.ENGLISH // 기본값
        }
    }
    
    fun setAppLanguage(context: Context, language: Language): Context {
        val locale = language.locale
        val config = Configuration(context.resources.configuration)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            return context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            return context
        }
    }
}

