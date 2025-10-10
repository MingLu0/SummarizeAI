package com.summarizeai.data.model

import com.summarizeai.data.model.SummaryData
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class StreamingResultTest {

    @Test
    fun `Progress should contain text and be identifiable`() {
        // Given
        val text = "This is a partial summary."
        
        // When
        val result = StreamingResult.Progress(text)
        
        // Then
        assertTrue(result is StreamingResult.Progress)
        assertEquals(text, result.text)
    }

    @Test
    fun `Complete should contain SummaryData and be identifiable`() {
        // Given
        val summaryData = SummaryData(
            id = "test-id",
            originalText = "Original text",
            shortSummary = "Short summary",
            mediumSummary = "Medium summary",
            detailedSummary = "Detailed summary",
            createdAt = Date(),
            isSaved = false
        )
        
        // When
        val result = StreamingResult.Complete(summaryData)
        
        // Then
        assertTrue(result is StreamingResult.Complete)
        assertEquals(summaryData, result.summaryData)
    }

    @Test
    fun `Error should contain message and be identifiable`() {
        // Given
        val errorMessage = "Network error occurred"
        
        // When
        val result = StreamingResult.Error(errorMessage)
        
        // Then
        assertTrue(result is StreamingResult.Error)
        assertEquals(errorMessage, result.message)
    }

    @Test
    fun `Progress instances with same text should be equal`() {
        // Given
        val text = "Same text"
        val result1 = StreamingResult.Progress(text)
        val result2 = StreamingResult.Progress(text)
        
        // Then
        assertEquals(result1, result2)
        assertEquals(result1.hashCode(), result2.hashCode())
    }

    @Test
    fun `Error instances with same message should be equal`() {
        // Given
        val message = "Same error"
        val result1 = StreamingResult.Error(message)
        val result2 = StreamingResult.Error(message)
        
        // Then
        assertEquals(result1, result2)
        assertEquals(result1.hashCode(), result2.hashCode())
    }

    @Test
    fun `Complete instances with same SummaryData should be equal`() {
        // Given
        val summaryData = SummaryData(
            id = "same-id",
            originalText = "Same text",
            shortSummary = "Same short",
            mediumSummary = "Same medium",
            detailedSummary = "Same detailed",
            createdAt = Date(1234567890L),
            isSaved = false
        )
        val result1 = StreamingResult.Complete(summaryData)
        val result2 = StreamingResult.Complete(summaryData)
        
        // Then
        assertEquals(result1, result2)
        assertEquals(result1.hashCode(), result2.hashCode())
    }
}
