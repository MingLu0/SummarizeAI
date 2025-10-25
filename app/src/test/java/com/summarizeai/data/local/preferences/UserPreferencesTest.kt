package com.summarizeai.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserPreferencesTest {

    private lateinit var mockDataStore: DataStore<Preferences>
    private val streamingEnabledKey = booleanPreferencesKey("streaming_enabled")

    @Before
    fun setup() {
        mockDataStore = mock()
    }

    @Test
    fun `isStreamingEnabled should return true by default`() = runTest {
        // Given
        val emptyPrefs = emptyPreferences()
        whenever(mockDataStore.data).thenReturn(flowOf(emptyPrefs))
        val userPreferences = UserPreferences(mockDataStore)

        // When
        val result = userPreferences.isStreamingEnabled.first()

        // Then
        assertTrue(result)
    }

    @Test
    fun `setStreamingEnabled should complete without error when setting to true`() = runTest {
        // Given
        val emptyPrefs = emptyPreferences()
        whenever(mockDataStore.data).thenReturn(flowOf(emptyPrefs))
        val userPreferences = UserPreferences(mockDataStore)
        
        // When/Then - should complete without throwing
        userPreferences.setStreamingEnabled(true)
        // If we get here, the test passed
        assertTrue(true)
    }

    @Test
    fun `setStreamingEnabled should complete without error when setting to false`() = runTest {
        // Given
        val emptyPrefs = emptyPreferences()
        whenever(mockDataStore.data).thenReturn(flowOf(emptyPrefs))
        val userPreferences = UserPreferences(mockDataStore)
        
        // When/Then - should complete without throwing
        userPreferences.setStreamingEnabled(false)
        // If we get here, the test passed
        assertTrue(true)
    }
}
