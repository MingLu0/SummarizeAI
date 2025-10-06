package com.summarizeai.data.remote.extractor

import com.summarizeai.utils.NetworkUtils
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class WebContentExtractorTest {

    @Mock
    private lateinit var networkUtils: NetworkUtils

    private lateinit var webContentExtractor: WebContentExtractor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        // Mock network as available for tests
        whenever(networkUtils.isNetworkAvailable()).thenReturn(true)
        webContentExtractor = WebContentExtractor(networkUtils)
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
        // Check if the error message contains the expected text or is about invalid URL
        val message = exception?.message ?: ""
        assertTrue("Expected error about HTTP/HTTPS or invalid URL, but got: $message", 
            message.contains("Only HTTP and HTTPS URLs") || message.contains("Invalid URL"))
    }

    @Test
    fun `extractContent should accept valid HTTP URLs`() = runTest {
        // Test valid HTTP URL (this will likely fail due to network, but URL validation should pass)
        val result = webContentExtractor.extractContent("https://example.com")
        // We expect this to fail due to network/timeout, but URL validation should pass
        // The failure should not be about invalid URL format
        if (result.isFailure) {
            val exception = result.exceptionOrNull()
            assertFalse(exception?.message?.contains("Invalid URL") == true)
            assertFalse(exception?.message?.contains("Only HTTP and HTTPS URLs") == true)
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
}
