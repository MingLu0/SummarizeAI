package com.summarizeai.data.remote.extractor

import com.summarizeai.utils.NetworkUtils
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for WebContentExtractor.
 * Note: These tests verify URL validation and network checks.
 * Full extraction tests with real websites are in integration tests.
 * 
 * Uses Robolectric to mock Android framework classes like Log.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class WebContentExtractorTest {

    @Mock
    private lateinit var networkUtils: NetworkUtils

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var webContentExtractor: WebContentExtractor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        
        // Mock network as available for tests
        whenever(networkUtils.isNetworkAvailable()).thenReturn(true)
        okHttpClient = OkHttpClient()
        webContentExtractor = WebContentExtractor(networkUtils, okHttpClient)
    }

    @Test
    fun `extractContent should validate URL format`() = runTest {
        // Test invalid URL
        val result = webContentExtractor.extractContent("invalid-url")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Invalid URL") == true)
    }

    @Test
    fun `extractContent should reject non-HTTP URLs`() = runTest {
        // Test FTP URL
        val result = webContentExtractor.extractContent("ftp://example.com")
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        // Check if the error message contains the expected text
        val message = exception?.message ?: ""
        assertTrue("Expected error about HTTP/HTTPS, but got: $message", 
            message.contains("Only HTTP and HTTPS URLs") || message.contains("Invalid URL"))
    }

    @Test
    fun `extractContent should accept valid HTTP URLs`() = runTest {
        // Test valid HTTP URL (this will fail due to network/timeout, but URL validation should pass)
        val result = webContentExtractor.extractContent("https://example.com")
        // We expect this to fail due to network/timeout, but URL validation should pass
        // The failure should not be about invalid URL format
        if (result.isFailure) {
            val exception = result.exceptionOrNull()
            val message = exception?.message ?: ""
            // Should not fail on URL validation
            assertFalse("Should not fail on URL validation: $message", 
                message.contains("Invalid URL format"))
        }
    }

    @Test
    fun `extractContent should fail when network is not available`() = runTest {
        // Mock network as unavailable
        whenever(networkUtils.isNetworkAvailable()).thenReturn(false)
        
        val result = webContentExtractor.extractContent("https://example.com")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("No network connection available") == true)
    }

    @Test
    fun `WebContent data class works correctly`() {
        val content = WebContent(
            title = "Test Title",
            content = "Test Content",
            url = "https://example.com"
        )
        
        assertEquals("Test Title", content.title)
        assertEquals("Test Content", content.content)
        assertEquals("https://example.com", content.url)
    }
}
