package com.summarizeai.presentation.viewmodel

import com.summarizeai.data.local.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var mockUserPreferences: UserPreferences
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockUserPreferences = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isStreamingEnabled should return true by default`() = runTest {
        // Given
        whenever(mockUserPreferences.isStreamingEnabled).thenReturn(flowOf(true))
        val settingsViewModel = SettingsViewModel(mockUserPreferences)

        // When
        val result = settingsViewModel.isStreamingEnabled.first()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isStreamingEnabled should return false when preference is false`() = runTest {
        // Given
        whenever(mockUserPreferences.isStreamingEnabled).thenReturn(flowOf(false))
        val settingsViewModel = SettingsViewModel(mockUserPreferences)

        // When
        val result = settingsViewModel.isStreamingEnabled.first()

        // Then
        assertFalse(result)
    }

    @Test
    fun `setStreamingEnabled should call UserPreferences with true`() = runTest {
        // Given
        whenever(mockUserPreferences.isStreamingEnabled).thenReturn(flowOf(true))
        val settingsViewModel = SettingsViewModel(mockUserPreferences)

        // When
        settingsViewModel.setStreamingEnabled(true)

        // Then
        verify(mockUserPreferences).setStreamingEnabled(true)
    }

    @Test
    fun `setStreamingEnabled should call UserPreferences with false`() = runTest {
        // Given
        whenever(mockUserPreferences.isStreamingEnabled).thenReturn(flowOf(false))
        val settingsViewModel = SettingsViewModel(mockUserPreferences)

        // When
        settingsViewModel.setStreamingEnabled(false)

        // Then
        verify(mockUserPreferences).setStreamingEnabled(false)
    }

    @Test
    fun `isStreamingEnabled should emit updates when preference changes`() = runTest {
        // Given
        whenever(mockUserPreferences.isStreamingEnabled).thenReturn(flowOf(true, false, true))
        val settingsViewModel = SettingsViewModel(mockUserPreferences)

        // When
        val results = settingsViewModel.isStreamingEnabled.take(3).toList()

        // Then
        assertEquals(3, results.size)
        assertTrue(results[0])
        assertFalse(results[1])
        assertTrue(results[2])
    }
}
