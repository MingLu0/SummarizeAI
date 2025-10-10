package com.summarizeai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.summarizeai.data.local.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    val isStreamingEnabled: Flow<Boolean> = userPreferences.isStreamingEnabled
    
    fun setStreamingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setStreamingEnabled(enabled)
        }
    }
}
