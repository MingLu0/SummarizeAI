package com.nutshell.presentation.viewmodel

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Unit tests for WebContentViewModel
 *
 * Tests the share intent handling and URL extraction functionality.
 * Uses Robolectric to mock Android framework classes.
 */
@RunWith(RobolectricTestRunner::class)
class WebContentViewModelTest {

    private lateinit var viewModel: WebContentViewModel

    @Before
    fun setup() {
        viewModel = WebContentViewModel()
    }

    @Test
    fun `handleIntent with valid SEND action and URL updates state with extracted URL`() {
        // Arrange
        val expectedUrl = "https://www.bbc.com/news/articles/c874nw4g2zzo"
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, "Check out this article: $expectedUrl")
        }

        // Act
        viewModel.handleIntent(intent)

        // Assert
        val state = viewModel.uiState.value
        assertEquals("Should extract URL from shared text", expectedUrl, state.extractedContent)
        assertEquals("Should set sharedUrl", expectedUrl, state.sharedUrl)
        assertFalse("Should not be extracting", state.isExtracting)
    }

    @Test
    fun `handleIntent with URL only updates state with URL`() {
        // Arrange
        val expectedUrl = "https://example.com/article"
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, expectedUrl)
        }

        // Act
        viewModel.handleIntent(intent)

        // Assert
        assertEquals("Should extract URL from plain URL text", expectedUrl, viewModel.uiState.value.extractedContent)
    }

    @Test
    fun `handleIntent with https URL updates state with correct URL`() {
        // Arrange
        val expectedUrl = "https://secure.example.com/path"
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, "Share: $expectedUrl")
        }

        // Act
        viewModel.handleIntent(intent)

        // Assert
        assertEquals("Should handle https URLs", expectedUrl, viewModel.uiState.value.extractedContent)
    }

    @Test
    fun `handleIntent with http URL updates state with correct URL`() {
        // Arrange
        val expectedUrl = "http://example.com/path"
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, "Check: $expectedUrl")
        }

        // Act
        viewModel.handleIntent(intent)

        // Assert
        assertEquals("Should handle http URLs", expectedUrl, viewModel.uiState.value.extractedContent)
    }

    @Test
    fun `handleIntent with no EXTRA_TEXT does not update state`() {
        // Arrange
        val intent = Intent(Intent.ACTION_SEND)
        // No EXTRA_TEXT set

        // Act
        viewModel.handleIntent(intent)

        // Assert
        assertNull("Should not set extractedContent when no text is shared", viewModel.uiState.value.extractedContent)
    }

    @Test
    fun `handleIntent with text but no URL does not update state`() {
        // Arrange
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, "Just some plain text without a URL")
        }

        // Act
        viewModel.handleIntent(intent)

        // Assert
        assertNull("Should not set extractedContent when no URL found in text", viewModel.uiState.value.extractedContent)
    }

    @Test
    fun `handleIntent with null intent does not update state`() {
        // Arrange
        val intent: Intent? = null

        // Act
        viewModel.handleIntent(intent)

        // Assert
        assertNull("Should not set extractedContent for null intent", viewModel.uiState.value.extractedContent)
    }

    @Test
    fun `handleIntent with wrong action does not update state`() {
        // Arrange
        val intent = Intent(Intent.ACTION_VIEW).apply {
            putExtra(Intent.EXTRA_TEXT, "https://example.com")
        }

        // Act
        viewModel.handleIntent(intent)

        // Assert
        assertNull("Should not set extractedContent for non-SEND actions", viewModel.uiState.value.extractedContent)
    }

    @Test
    fun `handleIntent with multiple URLs extracts first URL`() {
        // Arrange
        val firstUrl = "https://first.com"
        val secondUrl = "https://second.com"
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, "Check $firstUrl and also $secondUrl")
        }

        // Act
        viewModel.handleIntent(intent)

        // Assert
        assertEquals("Should extract first URL when multiple found", firstUrl, viewModel.uiState.value.extractedContent)
    }

    @Test
    fun `handleIntent with URL containing query parameters preserves full URL`() {
        // Arrange
        val expectedUrl = "https://example.com/article?id=123&source=share"
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, expectedUrl)
        }

        // Act
        viewModel.handleIntent(intent)

        // Assert
        assertEquals("Should preserve query parameters", expectedUrl, viewModel.uiState.value.extractedContent)
    }

    @Test
    fun `handleIntent with URL containing fragment preserves full URL`() {
        // Arrange
        val expectedUrl = "https://example.com/article#section-2"
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, expectedUrl)
        }

        // Act
        viewModel.handleIntent(intent)

        // Assert
        assertEquals("Should preserve URL fragment", expectedUrl, viewModel.uiState.value.extractedContent)
    }

    @Test
    fun `handleIntent updates both extractedContent and sharedUrl in state`() {
        // Arrange
        val expectedUrl = "https://example.com"
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, expectedUrl)
        }

        // Act
        viewModel.handleIntent(intent)

        // Assert
        val state = viewModel.uiState.value
        assertEquals("Should update extractedContent in state", expectedUrl, state.extractedContent)
        assertEquals("Should update sharedUrl in state", expectedUrl, state.sharedUrl)
    }

    @Test
    fun `clearExtractedContent resets state`() {
        // Arrange
        val url = "https://example.com"
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, url)
        }
        viewModel.handleIntent(intent)

        // Act
        viewModel.clearExtractedContent()

        // Assert
        val state = viewModel.uiState.value
        assertNull("Should clear extractedContent", state.extractedContent)
        assertNull("Should clear sharedUrl", state.sharedUrl)
    }
}
