package com.summarizeai.data.remote.api

import org.junit.Assert.*
import org.junit.Test

class StreamChunkTest {

    @Test
    fun `StreamChunk should contain content and done flag`() {
        // Given
        val content = "This is streaming content"
        val done = false
        
        // When
        val chunk = StreamChunk(content, done)
        
        // Then
        assertEquals(content, chunk.content)
        assertEquals(done, chunk.done)
    }

    @Test
    fun `StreamChunk with done=true should indicate completion`() {
        // Given
        val content = "Final content"
        val done = true
        
        // When
        val chunk = StreamChunk(content, done)
        
        // Then
        assertTrue(chunk.done)
        assertEquals(content, chunk.content)
    }

    @Test
    fun `StreamChunk with done=false should indicate continuation`() {
        // Given
        val content = "Partial content"
        val done = false
        
        // When
        val chunk = StreamChunk(content, done)
        
        // Then
        assertFalse(chunk.done)
        assertEquals(content, chunk.content)
    }

    @Test
    fun `StreamChunk instances with same data should be equal`() {
        // Given
        val content = "Same content"
        val done = true
        val chunk1 = StreamChunk(content, done)
        val chunk2 = StreamChunk(content, done)
        
        // Then
        assertEquals(chunk1, chunk2)
        assertEquals(chunk1.hashCode(), chunk2.hashCode())
    }

    @Test
    fun `StreamChunk instances with different data should not be equal`() {
        // Given
        val chunk1 = StreamChunk("Content 1", false)
        val chunk2 = StreamChunk("Content 2", false)
        
        // Then
        assertNotEquals(chunk1, chunk2)
    }

    @Test
    fun `StreamChunk should handle empty content`() {
        // Given
        val content = ""
        val done = false
        
        // When
        val chunk = StreamChunk(content, done)
        
        // Then
        assertEquals("", chunk.content)
        assertFalse(chunk.done)
    }
}
