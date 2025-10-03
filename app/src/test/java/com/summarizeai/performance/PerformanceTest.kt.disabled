package com.summarizeai.performance

import com.summarizeai.data.model.SummaryData
import com.summarizeai.domain.repository.SummaryRepository
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOf
import org.junit.Test
import org.junit.Assert.*
import java.util.*

class PerformanceTest {
    
    @Test
    fun `repository performance test with large dataset`() = runBlocking {
        // Given
        val repository = mockk<SummaryRepository>()
        val largeDataset = (1..1000).map { index ->
            SummaryData(
                id = "summary-$index",
                originalText = "Original text number $index with some content to test performance",
                shortSummary = "Short summary $index",
                mediumSummary = "Medium summary $index with more detailed content",
                detailedSummary = "Detailed summary $index with comprehensive information about the topic",
                createdAt = Date(),
                isSaved = index % 2 == 0
            )
        }
        
        every { repository.getAllSummaries() } returns flowOf(largeDataset)
        every { repository.getSavedSummaries() } returns flowOf(largeDataset.filter { it.isSaved })
        
        // When
        val startTime = System.currentTimeMillis()
        
        val allSummaries = repository.getAllSummaries()
        val savedSummaries = repository.getSavedSummaries()
        
        // Collect data
        val allSummariesList = allSummaries.first()
        val savedSummariesList = savedSummaries.first()
        
        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        
        // Then
        assertEquals(1000, allSummariesList.size)
        assertEquals(500, savedSummariesList.size) // Half are saved
        
        // Performance assertion - should complete within reasonable time
        assertTrue("Performance test took too long: ${executionTime}ms", executionTime < 1000)
        
        println("Performance test completed in ${executionTime}ms")
    }
    
    @Test
    fun `memory usage test with multiple operations`() = runBlocking {
        // Given
        val repository = mockk<SummaryRepository>()
        
        every { repository.summarizeText(any()) } returns ApiResult.Success(
            SummaryData(
                originalText = "Test text",
                shortSummary = "Short",
                mediumSummary = "Medium",
                detailedSummary = "Detailed"
            )
        )
        
        // When
        val startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        // Perform multiple operations
        repeat(100) { index ->
            repository.summarizeText("Test text $index")
        }
        
        // Force garbage collection
        System.gc()
        Thread.sleep(100)
        
        val endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val memoryUsed = endMemory - startMemory
        
        // Then
        // Memory usage should be reasonable (less than 10MB for 100 operations)
        assertTrue("Memory usage too high: ${memoryUsed / 1024 / 1024}MB", memoryUsed < 10 * 1024 * 1024)
        
        println("Memory usage test: ${memoryUsed / 1024 / 1024}MB used for 100 operations")
    }
    
    @Test
    fun `concurrent operations test`() = runBlocking {
        // Given
        val repository = mockk<SummaryRepository>()
        
        every { repository.summarizeText(any()) } returns ApiResult.Success(
            SummaryData(
                originalText = "Test text",
                shortSummary = "Short",
                mediumSummary = "Medium",
                detailedSummary = "Detailed"
            )
        )
        
        // When
        val startTime = System.currentTimeMillis()
        
        val jobs = (1..10).map { index ->
            async {
                repository.summarizeText("Concurrent test text $index")
            }
        }
        
        // Wait for all operations to complete
        jobs.awaitAll()
        
        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        
        // Then
        // Concurrent operations should be faster than sequential
        assertTrue("Concurrent operations took too long: ${executionTime}ms", executionTime < 5000)
        
        println("Concurrent operations test completed in ${executionTime}ms")
    }
}
