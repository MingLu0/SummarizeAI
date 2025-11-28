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
    DARK,
}

enum class SummaryLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    CHINESE("zh-CN", "中文"),
    ;

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
    LONG("Long"),
}

enum class ApiVersion(val displayName: String) {
    V3("V3 - Classic"),
    V4("V4 - Structured Streaming (Beta)"),
}

enum class SummaryStyle(val value: String, val displayName: String, val description: String) {
    SKIMMER("skimmer", "Skimmer", "Quick overview with key points"),
    EXECUTIVE("executive", "Executive", "Balanced summary for professionals"),
    ELI5("eli5", "ELI5", "Simple explanation, easy to understand"),
}

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    private val streamingEnabledKey = booleanPreferencesKey("streaming_enabled")
    private val themeModeKey = stringPreferencesKey("theme_mode")
    private val summaryLanguageKey = stringPreferencesKey("summary_language")
    private val summaryLengthKey = stringPreferencesKey("summary_length")
    private val apiVersionKey = stringPreferencesKey("api_version")
    private val summaryStyleKey = stringPreferencesKey("summary_style")

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

    val apiVersion: Flow<ApiVersion> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val versionString = preferences[apiVersionKey] ?: ApiVersion.V3.name
            try {
                ApiVersion.valueOf(versionString)
            } catch (e: IllegalArgumentException) {
                ApiVersion.V3
            }
        }

    val summaryStyle: Flow<SummaryStyle> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val styleString = preferences[summaryStyleKey] ?: SummaryStyle.EXECUTIVE.name
            try {
                SummaryStyle.valueOf(styleString)
            } catch (e: IllegalArgumentException) {
                SummaryStyle.EXECUTIVE
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

    suspend fun setApiVersion(version: ApiVersion) {
        dataStore.edit { preferences ->
            preferences[apiVersionKey] = version.name
        }
    }

    suspend fun setSummaryStyle(style: SummaryStyle) {
        dataStore.edit { preferences ->
            preferences[summaryStyleKey] = style.name
        }
    }
}
