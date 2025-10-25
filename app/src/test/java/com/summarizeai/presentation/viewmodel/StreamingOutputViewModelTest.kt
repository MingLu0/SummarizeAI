package com.summarizeai.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.summarizeai.data.model.StreamingResult
import com.summarizeai.data.model.SummaryData
import com.summarizeai.domain.repository.SummaryRepository
import com.summarizeai.utils.ClipboardUtils
import com.summarizeai.utils.ShareUtils
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class StreamingOutputViewModelTest {

    private lateinit var mockRepository: SummaryRepository
    private lateinit var mockClipboardUtils: ClipboardUtils
    private lateinit var mockShareUtils: ShareUtils
    private lateinit var mockSavedStateHandle: SavedStateHandle
    private lateinit var streamingOutputViewModel: StreamingOutputViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mock()
        mockClipboardUtils = mock()
        mockShareUtils = mock()
        mockSavedStateHandle = mock()
        streamingOutputViewModel = StreamingOutputViewModel(
            mockRepository,
            mockClipboardUtils,
            mockShareUtils,
            mockSavedStateHandle
        )
    }

    @org.junit.After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startStreaming should initiate streaming and update state`() = runTest {
        // Given
        val testText = "Test text to summarize"
        val summaryData = SummaryData(
            id = "test-id",
            originalText = testText,
            shortSummary = "Short summary",
            mediumSummary = "First sentence. Second sentence.",
            detailedSummary = "Detailed summary",
            createdAt = java.util.Date(),
            isSaved = false
        )
        val streamingResults = listOf(
            StreamingResult.Progress("First sentence."),
            StreamingResult.Progress("Second sentence."),
            StreamingResult.Complete(summaryData)
        )
        
        whenever(mockRepository.summarizeTextStreaming(eq(testText))).thenReturn(flowOf(*streamingResults.toTypedArray()))

        // When
        streamingOutputViewModel.startStreaming(testText)
        advanceUntilIdle() // Wait for all coroutines to complete

        // Then
        verify(mockRepository).summarizeTextStreaming(testText)
        
        val uiState = streamingOutputViewModel.uiState.value
        assertFalse(uiState.isStreaming)
        assertNotNull(uiState.summaryData)
        assertEquals("First sentence. Second sentence.", uiState.summaryData?.mediumSummary)
    }

    @Test
    fun `startStreaming should handle empty input text`() = runTest {
        // Given
        val emptyText = ""

        // When
        streamingOutputViewModel.startStreaming(emptyText)
        advanceUntilIdle()

        // Then
        verify(mockRepository, never()).summarizeTextStreaming(any())
        
        val uiState = streamingOutputViewModel.uiState.value
        assertEquals("Input text is empty", uiState.error)
    }

    @Test
    fun `startStreaming should handle streaming errors`() = runTest {
        // Given
        val testText = "Test text"
        val errorMessage = "Streaming error occurred"
        val streamingResults = listOf(
            StreamingResult.Progress("First sentence."),
            StreamingResult.Error(errorMessage)
        )
        
        whenever(mockRepository.summarizeTextStreaming(eq(testText))).thenReturn(flowOf(*streamingResults.toTypedArray()))

        // When
        streamingOutputViewModel.startStreaming(testText)
        advanceUntilIdle() // Wait for all coroutines to complete

        // Then
        val uiState = streamingOutputViewModel.uiState.value
        assertFalse(uiState.isStreaming)
        assertEquals(errorMessage, uiState.error)
    }

    @Test
    fun `getCurrentSummaryText should return streaming text during streaming`() = runTest {
        // Given
        val testText = "Test text"
        val summaryData = SummaryData(
            id = "test-id",
            originalText = testText,
            shortSummary = "Short",
            mediumSummary = "First sentence.",
            detailedSummary = "Detailed",
            createdAt = java.util.Date(),
            isSaved = false
        )
        val streamingResults = listOf(
            StreamingResult.Progress("First sentence."),
            StreamingResult.Complete(summaryData)
        )
        
        whenever(mockRepository.summarizeTextStreaming(eq(testText))).thenReturn(flowOf(*streamingResults.toTypedArray()))

        // When
        streamingOutputViewModel.startStreaming(testText)
        advanceUntilIdle() // Wait for all coroutines to complete
        
        // After completion, getCurrentSummaryText should return the medium summary
        val currentText = streamingOutputViewModel.getCurrentSummaryText()

        // Then
        assertEquals("First sentence.", currentText)
    }

    @Test
    fun `getCurrentSummaryText should return appropriate summary based on tab when completed`() = runTest {
        // Given
        val testText = "Test text"
        val summaryData = SummaryData(
            id = "test-id",
            originalText = testText,
            shortSummary = "Short summary",
            mediumSummary = "Medium summary",
            detailedSummary = "Detailed summary",
            createdAt = java.util.Date(),
            isSaved = false
        )
        val streamingResults = listOf(
            StreamingResult.Complete(summaryData)
        )
        
        whenever(mockRepository.summarizeTextStreaming(eq(testText))).thenReturn(flowOf(*streamingResults.toTypedArray()))

        // When
        streamingOutputViewModel.startStreaming(testText)
        advanceUntilIdle() // Wait for all coroutines to complete
        
        // Test medium summary (default tab is index 1)
        var currentText = streamingOutputViewModel.getCurrentSummaryText()
        assertEquals("Medium summary", currentText)
        
        // Test short summary
        streamingOutputViewModel.selectTab(0)
        advanceUntilIdle()
        currentText = streamingOutputViewModel.getCurrentSummaryText()
        assertEquals("Short summary", currentText)
        
        // Test detailed summary
        streamingOutputViewModel.selectTab(2)
        advanceUntilIdle()
        currentText = streamingOutputViewModel.getCurrentSummaryText()
        assertEquals("Detailed summary", currentText)
    }

    @Test
    fun `copyToClipboard should copy current summary text`() = runTest {
        // Given
        val testText = "Test text"
        val summaryData = SummaryData(
            id = "test-id",
            originalText = testText,
            shortSummary = "Short summary",
            mediumSummary = "Medium summary",
            detailedSummary = "Detailed summary",
            createdAt = java.util.Date(),
            isSaved = false
        )
        val streamingResults = listOf(
            StreamingResult.Complete(summaryData)
        )
        
        whenever(mockRepository.summarizeTextStreaming(eq(testText))).thenReturn(flowOf(*streamingResults.toTypedArray()))

        // When
        streamingOutputViewModel.startStreaming(testText)
        advanceUntilIdle() // Wait for all coroutines to complete
        streamingOutputViewModel.copyToClipboard()

        // Then
        verify(mockClipboardUtils).copyToClipboard(any(), any())
    }

    @Test
    fun `shareSummary should share current summary text`() = runTest {
        // Given
        val testText = "Test text"
        val summaryData = SummaryData(
            id = "test-id",
            originalText = testText,
            shortSummary = "Short summary",
            mediumSummary = "Medium summary",
            detailedSummary = "Detailed summary",
            createdAt = java.util.Date(),
            isSaved = false
        )
        val streamingResults = listOf(
            StreamingResult.Complete(summaryData)
        )
        
        whenever(mockRepository.summarizeTextStreaming(eq(testText))).thenReturn(flowOf(*streamingResults.toTypedArray()))

        // When
        streamingOutputViewModel.startStreaming(testText)
        advanceUntilIdle() // Wait for all coroutines to complete
        streamingOutputViewModel.shareSummary()

        // Then
        verify(mockShareUtils).shareText(any(), any())
    }

    @Test
    fun `toggleSaveStatus should toggle save status and update local state`() = runTest {
        // Given
        val testText = "Test text"
        val summaryData = SummaryData(
            id = "test-id",
            originalText = testText,
            shortSummary = "Short summary",
            mediumSummary = "Medium summary",
            detailedSummary = "Detailed summary",
            createdAt = java.util.Date(),
            isSaved = false
        )
        val streamingResults = listOf(
            StreamingResult.Complete(summaryData)
        )
        
        whenever(mockRepository.summarizeTextStreaming(eq(testText))).thenReturn(flowOf(*streamingResults.toTypedArray()))

        // When
        streamingOutputViewModel.startStreaming(testText)
        advanceUntilIdle() // Wait for all coroutines to complete
        streamingOutputViewModel.toggleSaveStatus()
        advanceUntilIdle()

        // Then
        verify(mockRepository).toggleSaveStatus(any())
        
        val uiState = streamingOutputViewModel.uiState.value
        assertTrue(uiState.summaryData?.isSaved == true)
    }
}
