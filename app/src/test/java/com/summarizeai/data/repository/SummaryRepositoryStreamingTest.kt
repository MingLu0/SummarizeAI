package com.summarizeai.data.repository

import com.summarizeai.data.local.datasource.SummaryLocalDataSource
import com.summarizeai.data.model.StreamingResult
import com.summarizeai.data.remote.api.StreamChunk
import com.summarizeai.data.remote.api.StreamingSummarizerService
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class SummaryRepositoryStreamingTest {

    private lateinit var mockStreamingService: StreamingSummarizerService
    private lateinit var mockLocalDataSource: SummaryLocalDataSource
    private lateinit var repository: SummaryRepositoryImpl

    @Before
    fun setup() {
        mockStreamingService = mock()
        mockLocalDataSource = mock()
        val mockRemoteDataSource = mock<com.summarizeai.data.remote.datasource.SummaryRemoteDataSource>()
        repository = SummaryRepositoryImpl(mockRemoteDataSource, mockLocalDataSource, mockStreamingService)
    }

    @Test
    fun `summarizeTextStreaming should emit Progress events for complete sentences`() = runTest {
        // Given
        val testText = "This is a test. This is another sentence! And a third one?"
        val chunks = listOf(
            StreamChunk("This is a", false),
            StreamChunk(" test.", false),
            StreamChunk(" This is another", false),
            StreamChunk(" sentence!", false),
            StreamChunk(" And a third one?", true)
        )
        
        whenever(mockStreamingService.streamSummary(eq(testText), any())).thenReturn(flowOf(*chunks.toTypedArray()))

        // When
        val results = repository.summarizeTextStreaming(testText).toList()

        // Then
        assertEquals(3, results.size) // Should emit 3 complete sentences
        
        assertTrue(results[0] is StreamingResult.Progress)
        assertEquals("This is a test.", (results[0] as StreamingResult.Progress).text)
        
        assertTrue(results[1] is StreamingResult.Progress)
        assertEquals("This is another sentence!", (results[1] as StreamingResult.Progress).text)
        
        assertTrue(results[2] is StreamingResult.Complete)
        val summaryData = (results[2] as StreamingResult.Complete).summaryData
        assertEquals("And a third one?", summaryData.mediumSummary)
    }

    @Test
    fun `summarizeTextStreaming should buffer incomplete sentences`() = runTest {
        // Given
        val testText = "This is an incomplete"
        val chunks = listOf(
            StreamChunk("This is an", false),
            StreamChunk(" incomplete", true)
        )
        
        whenever(mockStreamingService.streamSummary(eq(testText), any())).thenReturn(flowOf(*chunks.toTypedArray()))

        // When
        val results = repository.summarizeTextStreaming(testText).toList()

        // Then
        assertEquals(1, results.size) // Should emit only when complete
        assertTrue(results[0] is StreamingResult.Complete)
        val summaryData = (results[0] as StreamingResult.Complete).summaryData
        assertEquals("This is an incomplete", summaryData.mediumSummary)
    }

    @Test
    fun `summarizeTextStreaming should save to database on completion`() = runTest {
        // Given
        val testText = "Test text."
        val chunks = listOf(StreamChunk("Test text.", true))
        
        whenever(mockStreamingService.streamSummary(eq(testText), any())).thenReturn(flowOf(*chunks.toTypedArray()))

        // When
        repository.summarizeTextStreaming(testText).toList()

        // Then
        verify(mockLocalDataSource).insertSummary(any())
    }

    @Test
    fun `summarizeTextStreaming should generate short and detailed summaries`() = runTest {
        // Given
        val testText = "This is a test sentence."
        val chunks = listOf(StreamChunk("This is a test sentence.", true))
        
        whenever(mockStreamingService.streamSummary(eq(testText), any())).thenReturn(flowOf(*chunks.toTypedArray()))

        // When
        val results = repository.summarizeTextStreaming(testText).toList()

        // Then
        val summaryData = (results[0] as StreamingResult.Complete).summaryData
        assertEquals("This is a test sentence.", summaryData.mediumSummary)
        assertTrue(summaryData.shortSummary.isNotEmpty())
        assertTrue(summaryData.detailedSummary.isNotEmpty())
        assertTrue(summaryData.detailedSummary.length > summaryData.mediumSummary.length)
    }

    @Test
    fun `summarizeTextStreaming should handle empty text`() = runTest {
        // Given
        val testText = ""
        val chunks = listOf(StreamChunk("", true))
        
        whenever(mockStreamingService.streamSummary(eq(testText), any())).thenReturn(flowOf(*chunks.toTypedArray()))

        // When
        val results = repository.summarizeTextStreaming(testText).toList()

        // Then
        assertEquals(1, results.size)
        assertTrue(results[0] is StreamingResult.Complete)
        val summaryData = (results[0] as StreamingResult.Complete).summaryData
        assertEquals("", summaryData.mediumSummary)
    }

    @Test
    fun `summarizeTextStreaming should handle multiple sentences with mixed punctuation`() = runTest {
        // Given
        val testText = "First sentence. Second sentence! Third sentence? Fourth sentence."
        val chunks = listOf(
            StreamChunk("First sentence.", false),
            StreamChunk(" Second sentence!", false),
            StreamChunk(" Third sentence?", false),
            StreamChunk(" Fourth sentence.", true)
        )
        
        whenever(mockStreamingService.streamSummary(eq(testText), any())).thenReturn(flowOf(*chunks.toTypedArray()))

        // When
        val results = repository.summarizeTextStreaming(testText).toList()

        // Then
        assertEquals(4, results.size) // Should emit 4 complete sentences
        
        assertTrue(results[0] is StreamingResult.Progress)
        assertEquals("First sentence.", (results[0] as StreamingResult.Progress).text)
        
        assertTrue(results[1] is StreamingResult.Progress)
        assertEquals("Second sentence!", (results[1] as StreamingResult.Progress).text)
        
        assertTrue(results[2] is StreamingResult.Progress)
        assertEquals("Third sentence?", (results[2] as StreamingResult.Progress).text)
        
        assertTrue(results[3] is StreamingResult.Complete)
        val summaryData = (results[3] as StreamingResult.Complete).summaryData
        assertEquals("Fourth sentence.", summaryData.mediumSummary)
    }
}
