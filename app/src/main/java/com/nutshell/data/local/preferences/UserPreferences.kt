package com.nutshell.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class SummaryLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    CHINESE("zh-CN", "中文");

    companion object {
        fun fromCode(code: String): SummaryLanguage {
            return values().find { it.code == code } ?: ENGLISH
        }

        fun getSystemLanguage(): SummaryLanguage {
            val systemLocale = Locale.getDefault().language
            return when (systemLocale) {
                "zh" -> CHINESE
                else -> ENGLISH
            }
        }
    }
}

enum class SummaryLength(val displayName: String) {
    SHORT("Short"),
    MEDIUM("Medium"),
    LONG("Long")
}

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val streamingEnabledKey = booleanPreferencesKey("streaming_enabled")
    private val themeModeKey = stringPreferencesKey("theme_mode")
    private val summaryLanguageKey = stringPreferencesKey("summary_language")
    private val summaryLengthKey = stringPreferencesKey("summary_length")
    
    val isStreamingEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[streamingEnabledKey] ?: true // Default to true
        }
    
    val themeMode: Flow<ThemeMode> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeModeString = preferences[themeModeKey] ?: ThemeMode.SYSTEM.name
            try {
                ThemeMode.valueOf(themeModeString)
            } catch (e: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }
        }

    val summaryLanguage: Flow<SummaryLanguage> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val languageCode = preferences[summaryLanguageKey]
                ?: SummaryLanguage.getSystemLanguage().code
            SummaryLanguage.fromCode(languageCode)
        }

    val summaryLength: Flow<SummaryLength> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val lengthString = preferences[summaryLengthKey] ?: SummaryLength.SHORT.name
            try {
                SummaryLength.valueOf(lengthString)
            } catch (e: IllegalArgumentException) {
                SummaryLength.SHORT
            }
        }

    suspend fun setStreamingEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[streamingEnabledKey] = enabled
        }
    }
    
    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[themeModeKey] = mode.name
        }
    }

    suspend fun setSummaryLanguage(language: SummaryLanguage) {
        dataStore.edit { preferences ->
            preferences[summaryLanguageKey] = language.code
        }
    }

    suspend fun setSummaryLength(length: SummaryLength) {
        dataStore.edit { preferences ->
            preferences[summaryLengthKey] = length.name
        }
    }
}
