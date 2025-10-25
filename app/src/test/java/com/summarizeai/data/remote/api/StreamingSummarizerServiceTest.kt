package com.summarizeai.data.remote.api

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for StreamingSummarizerService.
 * Note: These tests verify the service can be instantiated and basic flow behavior.
 * Integration tests with actual backend are in StreamingIntegrationTest.kt.disabled
 */
class StreamingSummarizerServiceTest {

    private lateinit var streamingService: StreamingSummarizerService

    @Before
    fun setup() {
        val mockOkHttpClient = OkHttpClient.Builder().build()
        streamingService = StreamingSummarizerService(mockOkHttpClient)
    }

    @Test
    fun `streamSummary returns a Flow`() = runTest {
        // Given
        val testText = "This is a test text to summarize"
        val baseUrl = "https://test.example.com"

        // When
        val flow = streamingService.streamSummary(testText, baseUrl)

        // Then
        assertNotNull(flow)
        // Note: We don't collect the flow as it would require a real server
        // This test just verifies the method returns a Flow without throwing
    }

    @Test
    fun `StreamChunk data class works correctly`() {
        // Test the StreamChunk data class
        val chunk1 = StreamChunk("test content", false)
        assertEquals("test content", chunk1.content)
        assertFalse(chunk1.done)

        val chunk2 = StreamChunk("final content", true)
        assertEquals("final content", chunk2.content)
        assertTrue(chunk2.done)
    }

    @Test
    fun `StreamChunk equals and hashCode work correctly`() {
        val chunk1 = StreamChunk("content", false)
        val chunk2 = StreamChunk("content", false)
        val chunk3 = StreamChunk("different", false)

        assertEquals(chunk1, chunk2)
        assertEquals(chunk1.hashCode(), chunk2.hashCode())
        assertNotEquals(chunk1, chunk3)
    }

    @Test
    fun `StreamChunk copy works correctly`() {
        val original = StreamChunk("content", false)
        val copied = original.copy(done = true)

        assertEquals("content", copied.content)
        assertTrue(copied.done)
        assertFalse(original.done) // Original unchanged
    }

    @Test
    fun `service can be instantiated with OkHttpClient`() {
        // Given
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        // When
        val service = StreamingSummarizerService(okHttpClient)

        // Then
        assertNotNull(service)
    }
}
