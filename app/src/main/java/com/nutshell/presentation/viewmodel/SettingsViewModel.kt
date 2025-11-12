package com.nutshell.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutshell.data.local.preferences.SummaryLanguage
import com.nutshell.data.local.preferences.SummaryLength
import com.nutshell.data.local.preferences.ThemeMode
import com.nutshell.data.local.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
) : ViewModel() {

    val isStreamingEnabled: Flow<Boolean> = userPreferences.isStreamingEnabled
    val themeMode: Flow<ThemeMode> = userPreferences.themeMode
    val summaryLanguage: Flow<SummaryLanguage> = userPreferences.summaryLanguage
    val summaryLength: Flow<SummaryLength> = userPreferences.summaryLength

    fun setStreamingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setStreamingEnabled(enabled)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userPreferences.setThemeMode(mode)
        }
    }

    fun setSummaryLanguage(language: SummaryLanguage) {
        viewModelScope.launch {
            userPreferences.setSummaryLanguage(language)
        }
    }

    fun setSummaryLength(length: SummaryLength) {
        viewModelScope.launch {
            userPreferences.setSummaryLength(length)
        }
    }
}
