package com.summarizeai.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    
    private val streamingEnabledKey = booleanPreferencesKey("streaming_enabled")
    
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
    
    suspend fun setStreamingEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[streamingEnabledKey] = enabled
        }
    }
}
