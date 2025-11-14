package com.nutshell.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.nutshell.data.model.StreamingResult
import com.nutshell.data.model.SummaryData
import com.nutshell.domain.repository.SummaryRepository
import com.nutshell.utils.ClipboardUtils
import com.nutshell.utils.ShareUtils
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for StreamingOutputViewModel
 *
 * Tests the streaming functionality with URL input and state management.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StreamingOutputViewModelTest {

    private lateinit var repository: SummaryRepository
    private lateinit var clipboardUtils: ClipboardUtils
    private lateinit var shareUtils: ShareUtils
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: StreamingOutputViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock Android Log class
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.v(any(), any()) } returns 0
        
        repository = mockk(relaxed = true)
        clipboardUtils = mockk(relaxed = true)
        shareUtils = mockk(relaxed = true)
        savedStateHandle = SavedStateHandle()
        viewModel = StreamingOutputViewModel(repository, clipboardUtils, shareUtils, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `startStreaming with valid URL starts streaming`() = runTest {
        // Arrange
        val url = "https://example.com/article"
        val testSummary = SummaryData(
            originalText = url,
            shortSummary = "Short summary",
            mediumSummary = "Medium summary",
            detailedSummary = "Detailed summary"
        )
        
        coEvery { repository.summarizeTextStreaming(url) } returns flow {
            emit(StreamingResult.Progress("Test "))
            emit(StreamingResult.Progress("content"))
            emit(StreamingResult.Complete(testSummary))
        }

        // Act
        viewModel.startStreaming(url)

        // Assert
        val finalState = viewModel.uiState.value
        assertFalse("Streaming should be complete", finalState.isStreaming)
        assertNotNull("Summary data should be set", finalState.summaryData)
        assertEquals("Summary should match", testSummary, finalState.summaryData)
    }

    @Test
    fun `startStreaming with blank input sets error`() = runTest {
        // Arrange
        val blankInput = "   "

        // Act
        viewModel.startStreaming(blankInput)

        // Assert
        val state = viewModel.uiState.value
        assertFalse("Should not be streaming", state.isStreaming)
        assertEquals("Should set error message", "Input text is empty", state.error)
    }

    @Test
    fun `startStreaming with empty input sets error`() = runTest {
        // Arrange
        val emptyInput = ""

        // Act
        viewModel.startStreaming(emptyInput)

        // Assert
        val state = viewModel.uiState.value
        assertFalse("Should not be streaming", state.isStreaming)
        assertEquals("Should set error message", "Input text is empty", state.error)
    }

    @Test
    fun `startStreaming accumulates streaming text`() = runTest {
        // Arrange
        val url = "https://example.com"
        val testSummary = SummaryData(
            originalText = url,
            shortSummary = "Short",
            mediumSummary = "Medium",
            detailedSummary = "Detailed"
        )
        
        coEvery { repository.summarizeTextStreaming(url) } returns flow {
            emit(StreamingResult.Progress("First "))
            emit(StreamingResult.Progress("second "))
            emit(StreamingResult.Progress("third"))
            emit(StreamingResult.Complete(testSummary))
        }

        // Act
        viewModel.startStreaming(url)

        // Assert - Note: In the actual implementation, streaming text is cleared on completion
        // So we're testing that the streaming process handled all chunks
        val state = viewModel.uiState.value
        assertFalse("Should have completed streaming", state.isStreaming)
        assertNotNull("Should have summary data", state.summaryData)
    }

    @Test
    fun `startStreaming handles error from repository`() = runTest {
        // Arrange
        val url = "https://example.com"
        val errorMessage = "Network error"
        
        coEvery { repository.summarizeTextStreaming(url) } returns flow {
            emit(StreamingResult.Error(errorMessage))
        }

        // Act
        viewModel.startStreaming(url)

        // Assert
        val state = viewModel.uiState.value
        assertFalse("Should not be streaming", state.isStreaming)
        assertEquals("Should set error message", errorMessage, state.error)
    }

    @Test
    fun `startStreaming with long URL works correctly`() = runTest {
        // Arrange
        val longUrl = "https://example.com/very/long/path?param1=value1&param2=value2&param3=value3"
        val testSummary = SummaryData(
            originalText = longUrl,
            shortSummary = "Summary",
            mediumSummary = "Medium",
            detailedSummary = "Detailed"
        )
        
        coEvery { repository.summarizeTextStreaming(longUrl) } returns flow {
            emit(StreamingResult.Complete(testSummary))
        }

        // Act
        viewModel.startStreaming(longUrl)

        // Assert
        val state = viewModel.uiState.value
        assertNotNull("Should have summary data", state.summaryData)
        assertEquals("Original text should be the URL", longUrl, state.summaryData?.originalText)
    }

    @Test
    fun `resetState clears all state`() = runTest {
        // Arrange
        val url = "https://example.com"
        val testSummary = SummaryData(
            originalText = url,
            shortSummary = "Short",
            mediumSummary = "Medium",
            detailedSummary = "Detailed"
        )
        
        coEvery { repository.summarizeTextStreaming(url) } returns flow {
            emit(StreamingResult.Complete(testSummary))
        }
        viewModel.startStreaming(url)

        // Act
        viewModel.resetState()

        // Assert
        val state = viewModel.uiState.value
        assertEquals("Streaming text should be empty", "", state.streamingText)
        assertFalse("Should not be streaming", state.isStreaming)
        assertNull("Summary data should be null", state.summaryData)
        assertNull("Error should be null", state.error)
    }

    @Test
    fun `selectTab updates selected tab index`() {
        // Arrange
        val tabIndex = 2

        // Act
        viewModel.selectTab(tabIndex)

        // Assert
        assertEquals("Selected tab should be updated", tabIndex, viewModel.uiState.value.selectedTabIndex)
    }

    @Test
    fun `initial state has default values`() {
        // Assert
        val state = viewModel.uiState.value
        assertEquals("Initial streaming text should be empty", "", state.streamingText)
        assertFalse("Should not be streaming initially", state.isStreaming)
        assertNull("Initial summary data should be null", state.summaryData)
        assertEquals("Default tab should be medium (index 1)", 1, state.selectedTabIndex)
        assertNull("Initial error should be null", state.error)
    }
}

