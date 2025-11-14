package com.nutshell.navigation

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Unit tests for share intent navigation flow
 *
 * Tests URL encoding/decoding and route generation for shared URLs.
 * Uses Robolectric to mock Android framework classes.
 */
@RunWith(RobolectricTestRunner::class)
class ShareIntentNavigationTest {

    @Test
    fun `URL with query parameters is encoded correctly`() {
        // Arrange
        val url = "https://example.com/article?id=123&source=share"

        // Act
        val encoded = Uri.encode(url)
        val decoded = Uri.decode(encoded)

        // Assert
        assertEquals("Decoded URL should match original", url, decoded)
    }

    @Test
    fun `URL with fragment is encoded correctly`() {
        // Arrange
        val url = "https://example.com/article#section-2"

        // Act
        val encoded = Uri.encode(url)
        val decoded = Uri.decode(encoded)

        // Assert
        assertEquals("Decoded URL should match original", url, decoded)
    }

    @Test
    fun `URL with special characters is encoded correctly`() {
        // Arrange
        val url = "https://example.com/article?title=Hello%20World&tag=test+tag"

        // Act
        val encoded = Uri.encode(url)
        val decoded = Uri.decode(encoded)

        // Assert
        assertEquals("Decoded URL should match original", url, decoded)
    }

    @Test
    fun `BBC News URL is encoded correctly`() {
        // Arrange
        val url = "https://www.bbc.com/news/articles/c874nw4g2zzo"

        // Act
        val encoded = Uri.encode(url)
        val decoded = Uri.decode(encoded)

        // Assert
        assertEquals("BBC URL should encode/decode correctly", url, decoded)
    }

    @Test
    fun `URL with ampersand is encoded correctly`() {
        // Arrange
        val url = "https://example.com/search?q=android&lang=en"

        // Act
        val encoded = Uri.encode(url)
        val decoded = Uri.decode(encoded)

        // Assert
        assertEquals("URL with ampersand should encode/decode correctly", url, decoded)
    }

    @Test
    fun `URL with equals sign is encoded correctly`() {
        // Arrange
        val url = "https://example.com/article?param=value"

        // Act
        val encoded = Uri.encode(url)
        val decoded = Uri.decode(encoded)

        // Assert
        assertEquals("URL with equals should encode/decode correctly", url, decoded)
    }

    @Test
    fun `navigation route with sharedUrl parameter is formatted correctly`() {
        // Arrange
        val url = "https://example.com/article"
        val encodedUrl = Uri.encode(url)

        // Act
        val route = "main?sharedUrl=$encodedUrl"

        // Assert
        assert(route.startsWith("main?sharedUrl=")) {
            "Route should start with main?sharedUrl="
        }
        assert(route.contains(encodedUrl)) {
            "Route should contain encoded URL"
        }
    }

    @Test
    fun `long URL is encoded and decoded correctly`() {
        // Arrange
        val url = "https://www.example.com/very/long/path/to/article/with/many/segments?param1=value1&param2=value2&param3=value3#section"

        // Act
        val encoded = Uri.encode(url)
        val decoded = Uri.decode(encoded)

        // Assert
        assertEquals("Long URL should encode/decode correctly", url, decoded)
    }

    @Test
    fun `URL with Unicode characters is encoded correctly`() {
        // Arrange
        val url = "https://example.com/article?title=Ménü"

        // Act
        val encoded = Uri.encode(url)
        val decoded = Uri.decode(encoded)

        // Assert
        assertEquals("URL with Unicode should encode/decode correctly", url, decoded)
    }

    @Test
    fun `URL with slash in parameter is encoded correctly`() {
        // Arrange
        val url = "https://example.com/article?redirect=https://other.com/page"

        // Act
        val encoded = Uri.encode(url)
        val decoded = Uri.decode(encoded)

        // Assert
        assertEquals("URL with slash in param should encode/decode correctly", url, decoded)
    }

    @Test
    fun `empty sharedUrl parameter remains empty after encoding`() {
        // Arrange
        val emptyUrl = ""

        // Act
        val encoded = Uri.encode(emptyUrl)
        val decoded = Uri.decode(encoded)

        // Assert
        assertEquals("Empty URL should remain empty after encode/decode", "", decoded)
    }
}
