package com.summarizeai.data.remote.api

import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class StreamingSummarizerServiceTest {

    private lateinit var streamingService: StreamingSummarizerService

    @Before
    fun setup() {
        val mockOkHttpClient = OkHttpClient.Builder().build()
        streamingService = StreamingSummarizerService(mockOkHttpClient)
    }

    @Test
    fun `streamSummary should emit chunks from text`() = runTest {
        // Given
        val testText = "This is a test text to summarize"

        // When
        val chunks = streamingService.streamSummary(testText, "http://test.com")
            .toList()

        // Then
        assertTrue(chunks.isNotEmpty())
        assertFalse(chunks.dropLast(1).any { it.done }) // All except last should not be done
        assertTrue(chunks.last().done) // Last chunk should be done
    }

    @Test
    fun `streamSummary should handle short text`() = runTest {
        // Given
        val testText = "Short"

        // When
        val chunks = streamingService.streamSummary(testText, "http://test.com")
            .toList()

        // Then
        assertEquals(1, chunks.size)
        assertEquals("Short", chunks[0].content)
        assertTrue(chunks[0].done)
    }

    @Test
    fun `streamSummary should handle empty text`() = runTest {
        // Given
        val testText = ""

        // When
        val chunks = streamingService.streamSummary(testText, "http://test.com")
            .toList()

        // Then
        assertEquals(1, chunks.size)
        assertEquals("", chunks[0].content)
        assertTrue(chunks[0].done)
    }

    @Test
    fun `streamSummary should emit multiple chunks for long text`() = runTest {
        // Given
        val testText = "This is a very long text that should be split into multiple chunks for testing purposes"

        // When
        val chunks = streamingService.streamSummary(testText, "http://test.com")
            .toList()

        // Then
        assertTrue(chunks.size > 1)
        assertFalse(chunks.dropLast(1).any { it.done }) // All except last should not be done
        assertTrue(chunks.last().done) // Last chunk should be done
    }
}
