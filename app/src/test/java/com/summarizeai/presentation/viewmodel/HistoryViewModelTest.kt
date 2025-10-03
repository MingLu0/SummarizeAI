package com.summarizeai.presentation.viewmodel

import com.summarizeai.data.model.SummaryData
import com.summarizeai.domain.repository.SummaryRepository
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
import java.util.*

@ExperimentalCoroutinesApi
class HistoryViewModelTest {
    
    private lateinit var viewModel: HistoryViewModel
    private lateinit var repository: SummaryRepository
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        
        viewModel = HistoryViewModel(repository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `updateSearchQuery should update search query`() = runTest {
        // Given
        val searchQuery = "test search"
        
        // When
        viewModel.updateSearchQuery(searchQuery)
        
        // Then
        assertEquals(searchQuery, viewModel.searchQuery.first())
    }
    
    @Test
    fun `deleteSummary should call repository delete method`() = runTest {
        // Given
        val summary = SummaryData(
            id = "test-id",
            originalText = "test text",
            shortSummary = "short",
            mediumSummary = "medium",
            detailedSummary = "detailed"
        )
        
        coEvery { repository.deleteSummary(summary.id) } just Runs
        
        // When
        viewModel.deleteSummary(summary)
        
        // Then
        coVerify { repository.deleteSummary(summary.id) }
    }
    
    @Test
    fun `uiState should reflect repository data`() = runTest {
        // Given
        val summaries = listOf(
            SummaryData(
                id = "1",
                originalText = "text 1",
                shortSummary = "short 1",
                mediumSummary = "medium 1",
                detailedSummary = "detailed 1"
            ),
            SummaryData(
                id = "2",
                originalText = "text 2",
                shortSummary = "short 2",
                mediumSummary = "medium 2",
                detailedSummary = "detailed 2"
            )
        )
        
        every { repository.getAllSummaries() } returns flowOf(summaries)
        every { repository.getAllSummaries() } returns flowOf(summaries) // For search combine
        
        // When
        viewModel = HistoryViewModel(repository)
        
        // Then
        val uiState = viewModel.uiState.first()
        assertEquals(summaries, uiState.summaries)
        assertEquals(summaries, uiState.filteredSummaries)
        assertFalse(uiState.isLoading)
    }
    
    @Test
    fun `filteredSummaries should filter based on search query`() = runTest {
        // Given
        val summaries = listOf(
            SummaryData(
                id = "1",
                originalText = "apple text",
                shortSummary = "apple short",
                mediumSummary = "apple medium",
                detailedSummary = "apple detailed"
            ),
            SummaryData(
                id = "2",
                originalText = "banana text",
                shortSummary = "banana short",
                mediumSummary = "banana medium",
                detailedSummary = "banana detailed"
            )
        )
        
        every { repository.getAllSummaries() } returns flowOf(summaries)
        
        viewModel = HistoryViewModel(repository)
        
        // When
        viewModel.updateSearchQuery("apple")
        
        // Then
        val uiState = viewModel.uiState.first()
        assertEquals(1, uiState.filteredSummaries.size)
        assertEquals("apple text", uiState.filteredSummaries.first().originalText)
    }
}
