package com.summarizeai.presentation.viewmodel

import com.summarizeai.data.model.ApiResult
import com.summarizeai.data.model.SummaryData
import com.summarizeai.domain.repository.SummaryRepository
import com.summarizeai.utils.TextExtractionUtils
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class HomeViewModelTest {
    
    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: SummaryRepository
    private lateinit var textExtractionUtils: TextExtractionUtils
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        textExtractionUtils = mockk()
        
        viewModel = HomeViewModel(repository, textExtractionUtils)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `updateTextInput should update text and enable summarize button`() = runTest {
        // Given
        val testText = "Test input text"
        
        // When
        viewModel.updateTextInput(testText)
        
        // Then
        val uiState = viewModel.uiState.first()
        assertEquals(testText, uiState.textInput)
        assertTrue(uiState.isSummarizeEnabled)
    }
    
    @Test
    fun `updateTextInput with blank text should disable summarize button`() = runTest {
        // Given
        val blankText = ""
        
        // When
        viewModel.updateTextInput(blankText)
        
        // Then
        val uiState = viewModel.uiState.first()
        assertEquals(blankText, uiState.textInput)
        assertFalse(uiState.isSummarizeEnabled)
    }
    
    @Test
    fun `summarizeText with successful API call should update state`() = runTest {
        // Given
        val inputText = "Test text to summarize"
        val summaryData = SummaryData(
            originalText = inputText,
            shortSummary = "Short summary",
            mediumSummary = "Medium summary",
            detailedSummary = "Detailed summary"
        )
        
        coEvery { repository.summarizeText(inputText) } returns ApiResult.Success(summaryData)
        
        viewModel.updateTextInput(inputText)
        
        // When
        viewModel.summarizeText()
        
        // Then
        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertEquals(summaryData, uiState.summaryData)
        assertNull(uiState.error)
    }
    
    @Test
    fun `summarizeText with API error should update error state`() = runTest {
        // Given
        val inputText = "Test text to summarize"
        val errorMessage = "Network error"
        
        coEvery { repository.summarizeText(inputText) } returns ApiResult.Error(errorMessage)
        
        viewModel.updateTextInput(inputText)
        
        // When
        viewModel.summarizeText()
        
        // Then
        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertEquals(errorMessage, uiState.error)
        assertNull(uiState.summaryData)
    }
    
    @Test
    fun `clearError should remove error from state`() = runTest {
        // Given
        viewModel.updateTextInput("test")
        coEvery { repository.summarizeText("test") } returns ApiResult.Error("Test error")
        viewModel.summarizeText()
        
        // Verify error is set
        assertNotNull(viewModel.uiState.first().error)
        
        // When
        viewModel.clearError()
        
        // Then
        assertNull(viewModel.uiState.first().error)
    }
    
    @Test
    fun `resetState should return to initial state`() = runTest {
        // Given
        viewModel.updateTextInput("test")
        coEvery { repository.summarizeText("test") } returns ApiResult.Success(
            SummaryData(originalText = "test")
        )
        viewModel.summarizeText()
        
        // Verify state is modified
        val modifiedState = viewModel.uiState.first()
        assertTrue(modifiedState.textInput.isNotEmpty())
        
        // When
        viewModel.resetState()
        
        // Then
        val resetState = viewModel.uiState.first()
        assertTrue(resetState.textInput.isEmpty())
        assertFalse(resetState.isSummarizeEnabled)
        assertFalse(resetState.isLoading)
        assertNull(resetState.summaryData)
        assertNull(resetState.error)
    }
}
