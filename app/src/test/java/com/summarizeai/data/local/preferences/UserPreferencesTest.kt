package com.summarizeai.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class UserPreferencesTest {

    private lateinit var mockDataStore: DataStore<Preferences>
    private lateinit var userPreferences: UserPreferences

    @Before
    fun setup() {
        mockDataStore = mock()
        userPreferences = UserPreferences(mockDataStore)
    }

    @Test
    fun `isStreamingEnabled should return true by default`() = runTest {
        // Given
        val emptyPreferences = emptyPreferences()
        whenever(mockDataStore.data).thenReturn(flowOf(emptyPreferences))

        // When
        val result = userPreferences.isStreamingEnabled.first()

        // Then
        assertTrue(result)
    }

    @Test
    fun `setStreamingEnabled should call DataStore edit`() = runTest {
        // Given
        val mockPreferences = mock<Preferences>()
        whenever(mockDataStore.edit(any())).thenReturn(mockPreferences)

        // When
        userPreferences.setStreamingEnabled(true)

        // Then
        verify(mockDataStore).edit(any())
    }

    @Test
    fun `setStreamingEnabled should call DataStore edit with false`() = runTest {
        // Given
        val mockPreferences = mock<Preferences>()
        whenever(mockDataStore.edit(any())).thenReturn(mockPreferences)

        // When
        userPreferences.setStreamingEnabled(false)

        // Then
        verify(mockDataStore).edit(any())
    }
}
