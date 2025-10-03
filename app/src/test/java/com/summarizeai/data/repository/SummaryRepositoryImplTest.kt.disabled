package com.summarizeai.data.repository

import com.summarizeai.data.local.datasource.SummaryLocalDataSource
import com.summarizeai.data.local.database.SummaryEntity
import com.summarizeai.data.model.ApiResult
import com.summarizeai.data.model.SummaryData
import com.summarizeai.data.remote.api.SummarizerApi
import com.summarizeai.data.remote.api.SummarizeRequest
import com.summarizeai.data.remote.api.SummarizeResponse
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
import java.util.*

@ExperimentalCoroutinesApi
class SummaryRepositoryImplTest {
    
    private lateinit var repository: SummaryRepositoryImpl
    private lateinit var api: SummarizerApi
    private lateinit var localDataSource: SummaryLocalDataSource
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        api = mockk()
        localDataSource = mockk()
        
        repository = SummaryRepositoryImpl(api, localDataSource)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `summarizeText with successful API call should return success result`() = runTest {
        // Given
        val inputText = "Test text to summarize"
        val request = SummarizeRequest(text = inputText)
        val response = SummarizeResponse(
            summary = "Generated summary",
            model = "test-model",
            tokensUsed = 100,
            latencyMs = 500.0
        )
        val summaryData = SummaryData(
            originalText = inputText,
            shortSummary = "Generated summary",
            mediumSummary = "Generated summary",
            detailedSummary = "Generated summary This summary provides comprehensive insights into the original content, covering all major points and supporting details."
        )
        
        coEvery { api.summarize(request) } returns response
        coEvery { localDataSource.insertSummary(any()) } just Runs
        
        // When
        val result = repository.summarizeText(inputText)
        
        // Then
        assertTrue(result is ApiResult.Success)
        assertEquals(inputText, (result as ApiResult.Success).data.originalText)
        coVerify { localDataSource.insertSummary(any()) }
    }
    
    @Test
    fun `summarizeText with API error should return error result`() = runTest {
        // Given
        val inputText = "Test text to summarize"
        val request = SummarizeRequest(text = inputText)
        
        coEvery { api.summarize(request) } throws Exception("Network error")
        
        // When
        val result = repository.summarizeText(inputText)
        
        // Then
        assertTrue(result is ApiResult.Error)
        assertEquals("Network not available", (result as ApiResult.Error).message)
    }
    
    @Test
    fun `getAllSummaries should return data from local source`() = runTest {
        // Given
        val entities = listOf(
            SummaryEntity(
                id = "1",
                originalText = "text 1",
                shortSummary = "short 1",
                mediumSummary = "medium 1",
                detailedSummary = "detailed 1",
                createdAt = System.currentTimeMillis(),
                isSaved = false
            )
        )
        
        every { localDataSource.getAllSummaries() } returns flowOf(entities)
        
        // When
        val result = repository.getAllSummaries().first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("text 1", result.first().originalText)
    }
    
    @Test
    fun `getSavedSummaries should return only saved summaries`() = runTest {
        // Given
        val entities = listOf(
            SummaryEntity(
                id = "1",
                originalText = "text 1",
                shortSummary = "short 1",
                mediumSummary = "medium 1",
                detailedSummary = "detailed 1",
                createdAt = System.currentTimeMillis(),
                isSaved = true
            )
        )
        
        every { localDataSource.getSavedSummaries() } returns flowOf(entities)
        
        // When
        val result = repository.getSavedSummaries().first()
        
        // Then
        assertEquals(1, result.size)
        assertTrue(result.first().isSaved)
    }
    
    @Test
    fun `deleteSummary should call local data source`() = runTest {
        // Given
        val summaryId = "test-id"
        
        coEvery { localDataSource.deleteSummaryById(summaryId) } just Runs
        
        // When
        repository.deleteSummary(summaryId)
        
        // Then
        coVerify { localDataSource.deleteSummaryById(summaryId) }
    }
    
    @Test
    fun `toggleSaveStatus should update save status in local source`() = runTest {
        // Given
        val summaryId = "test-id"
        val entity = SummaryEntity(
            id = summaryId,
            originalText = "test",
            shortSummary = "short",
            mediumSummary = "medium",
            detailedSummary = "detailed",
            createdAt = System.currentTimeMillis(),
            isSaved = false
        )
        
        coEvery { localDataSource.getSummaryById(summaryId) } returns entity
        coEvery { localDataSource.updateSaveStatus(summaryId, true) } just Runs
        
        // When
        repository.toggleSaveStatus(summaryId)
        
        // Then
        coVerify { localDataSource.updateSaveStatus(summaryId, true) }
    }
    
    @Test
    fun `searchSummaries should return filtered results from local source`() = runTest {
        // Given
        val query = "test"
        val entities = listOf(
            SummaryEntity(
                id = "1",
                originalText = "test text",
                shortSummary = "short",
                mediumSummary = "medium",
                detailedSummary = "detailed",
                createdAt = System.currentTimeMillis(),
                isSaved = false
            )
        )
        
        every { localDataSource.searchSummaries(query) } returns flowOf(entities)
        
        // When
        val result = repository.searchSummaries(query).first()
        
        // Then
        assertEquals(1, result.size)
        assertTrue(result.first().originalText.contains(query, ignoreCase = true))
    }
}
