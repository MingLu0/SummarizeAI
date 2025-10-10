package com.summarizeai.presentation.viewmodel

import com.summarizeai.data.local.preferences.UserPreferences
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class SettingsViewModelTest {

    private lateinit var mockUserPreferences: UserPreferences
    private lateinit var settingsViewModel: SettingsViewModel

    @Before
    fun setup() {
        mockUserPreferences = mock()
        settingsViewModel = SettingsViewModel(mockUserPreferences)
    }

    @Test
    fun `isStreamingEnabled should return true by default`() = runTest {
        // Given
        whenever(mockUserPreferences.isStreamingEnabled).thenReturn(flowOf(true))

        // When
        val result = settingsViewModel.isStreamingEnabled.first()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isStreamingEnabled should return false when preference is false`() = runTest {
        // Given
        whenever(mockUserPreferences.isStreamingEnabled).thenReturn(flowOf(false))

        // When
        val result = settingsViewModel.isStreamingEnabled.first()

        // Then
        assertFalse(result)
    }

    @Test
    fun `setStreamingEnabled should call UserPreferences with true`() = runTest {
        // Given
        whenever(mockUserPreferences.setStreamingEnabled(true)).thenReturn(Unit)

        // When
        settingsViewModel.setStreamingEnabled(true)

        // Then
        verify(mockUserPreferences).setStreamingEnabled(true)
    }

    @Test
    fun `setStreamingEnabled should call UserPreferences with false`() = runTest {
        // Given
        whenever(mockUserPreferences.setStreamingEnabled(false)).thenReturn(Unit)

        // When
        settingsViewModel.setStreamingEnabled(false)

        // Then
        verify(mockUserPreferences).setStreamingEnabled(false)
    }

    @Test
    fun `isStreamingEnabled should emit updates when preference changes`() = runTest {
        // Given
        whenever(mockUserPreferences.isStreamingEnabled).thenReturn(flowOf(true, false, true))

        // When
        val results = settingsViewModel.isStreamingEnabled.take(3).toList()

        // Then
        assertEquals(3, results.size)
        assertTrue(results[0])
        assertFalse(results[1])
        assertTrue(results[2])
    }
}
