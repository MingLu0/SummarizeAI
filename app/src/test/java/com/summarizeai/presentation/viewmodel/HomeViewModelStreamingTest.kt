package com.summarizeai.presentation.viewmodel

import com.summarizeai.data.local.preferences.UserPreferences
import com.summarizeai.data.model.ApiResult
import com.summarizeai.data.model.StreamingResult
import com.summarizeai.data.model.SummaryData
import com.summarizeai.domain.repository.SummaryRepository
import com.summarizeai.utils.ErrorHandler
import com.summarizeai.utils.TextExtractionUtils
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class HomeViewModelStreamingTest {

    private lateinit var mockRepository: SummaryRepository
    private lateinit var mockTextExtractionUtils: TextExtractionUtils
    private lateinit var mockErrorHandler: ErrorHandler
    private lateinit var mockUserPreferences: UserPreferences
    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setup() {
        mockRepository = mock()
        mockTextExtractionUtils = mock()
        mockErrorHandler = mock()
        mockUserPreferences = mock()
        homeViewModel = HomeViewModel(
            mockRepository,
            mockTextExtractionUtils,
            mockErrorHandler,
            mockUserPreferences
        )
    }

    @Test
    fun `summarizeText should set navigation flag when streaming is enabled`() = runTest {
        // Given
        val testText = "Test text to summarize"
        
        whenever(mockUserPreferences.isStreamingEnabled).thenReturn(flowOf(true))

        // When
        homeViewModel.updateTextInput(testText)
        homeViewModel.summarizeText()

        // Then
        verify(mockUserPreferences).isStreamingEnabled
        verify(mockRepository, never()).summarizeText(any())
        verify(mockRepository, never()).summarizeTextStreaming(any())
        
        // Check UI state updates
        val uiState = homeViewModel.uiState.first()
        assertTrue(uiState.shouldNavigateToStreaming)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `summarizeText should use traditional API when streaming is disabled`() = runTest {
        // Given
        val testText = "Test text to summarize"
        val summaryData = SummaryData(
            id = "test-id",
            originalText = testText,
            shortSummary = "Short summary",
            mediumSummary = "Medium summary",
            detailedSummary = "Detailed summary",
            createdAt = java.util.Date(),
            isSaved = false
        )
        
        whenever(mockUserPreferences.isStreamingEnabled).thenReturn(flowOf(false))
        whenever(mockRepository.summarizeText(eq(testText))).thenReturn(ApiResult.Success(summaryData))

        // When
        homeViewModel.updateTextInput(testText)
        homeViewModel.summarizeText()

        // Then
        verify(mockUserPreferences).isStreamingEnabled
        verify(mockRepository).summarizeText(testText)
        verify(mockRepository, never()).summarizeTextStreaming(any())
        
        // Check UI state updates
        val uiState = homeViewModel.uiState.first()
        assertTrue(uiState.shouldNavigateToOutput)
        assertFalse(uiState.isLoading)
        assertNotNull(uiState.summaryData)
    }

    @Test
    fun `summarizeText should set navigation flag for streaming`() = runTest {
        // Given
        val testText = "Test text"
        
        whenever(mockUserPreferences.isStreamingEnabled).thenReturn(flowOf(true))

        // When
        homeViewModel.updateTextInput(testText)
        homeViewModel.summarizeText()

        // Then
        val uiState = homeViewModel.uiState.first()
        assertTrue(uiState.shouldNavigateToStreaming)
    }

    @Test
    fun `summarizeText should handle traditional API errors`() = runTest {
        // Given
        val testText = "Test text"
        val errorMessage = "API error occurred"
        
        whenever(mockUserPreferences.isStreamingEnabled).thenReturn(flowOf(false))
        whenever(mockRepository.summarizeText(eq(testText))).thenReturn(ApiResult.Error(errorMessage))

        // When
        homeViewModel.updateTextInput(testText)
        homeViewModel.summarizeText()

        // Then
        verify(mockErrorHandler).showErrorToast(errorMessage)
        
        val uiState = homeViewModel.uiState.first()
        assertFalse(uiState.shouldNavigateToStreaming)
        assertFalse(uiState.shouldNavigateToOutput)
        assertEquals(errorMessage, uiState.error)
    }


    @Test
    fun `summarizeText should not start if text is blank`() = runTest {
        // Given
        val blankText = ""
        
        // When
        homeViewModel.updateTextInput(blankText)
        homeViewModel.summarizeText()

        // Then
        verify(mockRepository, never()).summarizeText(any())
        verify(mockRepository, never()).summarizeTextStreaming(any())
        verify(mockUserPreferences, never()).isStreamingEnabled
    }

    @Test
    fun `summarizeText should set loading state initially`() = runTest {
        // Given
        val testText = "Test text"
        
        whenever(mockUserPreferences.isStreamingEnabled).thenReturn(flowOf(true))

        // When
        homeViewModel.updateTextInput(testText)
        homeViewModel.summarizeText()

        // Then
        val uiState = homeViewModel.uiState.first()
        assertTrue(uiState.isLoading || uiState.shouldNavigateToStreaming)
    }

    @Test
    fun `clearNavigationFlags should reset navigation flags`() = runTest {
        // Given
        val testText = "Test text"
        whenever(mockUserPreferences.isStreamingEnabled).thenReturn(flowOf(true))
        
        homeViewModel.updateTextInput(testText)
        homeViewModel.summarizeText()
        
        // Verify flags are set
        var uiState = homeViewModel.uiState.first()
        assertTrue(uiState.shouldNavigateToStreaming)

        // When
        homeViewModel.clearNavigationFlags()

        // Then
        uiState = homeViewModel.uiState.first()
        assertFalse(uiState.shouldNavigateToStreaming)
        assertFalse(uiState.shouldNavigateToOutput)
    }
}
