package com.summarizeai.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.summarizeai.data.local.database.SummarizeDatabase
import com.summarizeai.data.local.database.SummaryDao
import com.summarizeai.data.local.database.SummaryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    
    private lateinit var database: SummarizeDatabase
    private lateinit var dao: SummaryDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SummarizeDatabase::class.java
        ).allowMainThreadQueries().build()
        
        dao = database.summaryDao()
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun insertSummaryAndGetAllSummaries() = runTest {
        // Given
        val summary = SummaryEntity(
            id = "1",
            originalText = "Test text",
            shortSummary = "Short summary",
            mediumSummary = "Medium summary",
            detailedSummary = "Detailed summary",
            createdAt = System.currentTimeMillis(),
            isSaved = false
        )
        
        // When
        dao.insertSummary(summary)
        val summaries = dao.getAllSummaries().first()
        
        // Then
        assertEquals(1, summaries.size)
        assertEquals(summary.id, summaries.first().id)
        assertEquals(summary.originalText, summaries.first().originalText)
    }
    
    @Test
    fun insertMultipleSummariesAndGetAllSummaries() = runTest {
        // Given
        val summary1 = SummaryEntity(
            id = "1",
            originalText = "Test text 1",
            shortSummary = "Short summary 1",
            mediumSummary = "Medium summary 1",
            detailedSummary = "Detailed summary 1",
            createdAt = System.currentTimeMillis(),
            isSaved = false
        )
        
        val summary2 = SummaryEntity(
            id = "2",
            originalText = "Test text 2",
            shortSummary = "Short summary 2",
            mediumSummary = "Medium summary 2",
            detailedSummary = "Detailed summary 2",
            createdAt = System.currentTimeMillis() + 1000,
            isSaved = true
        )
        
        // When
        dao.insertSummary(summary1)
        dao.insertSummary(summary2)
        val summaries = dao.getAllSummaries().first()
        
        // Then
        assertEquals(2, summaries.size)
        // Should be ordered by createdAt DESC
        assertEquals(summary2.id, summaries.first().id)
        assertEquals(summary1.id, summaries.last().id)
    }
    
    @Test
    fun getSavedSummaries() = runTest {
        // Given
        val summary1 = SummaryEntity(
            id = "1",
            originalText = "Test text 1",
            shortSummary = "Short summary 1",
            mediumSummary = "Medium summary 1",
            detailedSummary = "Detailed summary 1",
            createdAt = System.currentTimeMillis(),
            isSaved = false
        )
        
        val summary2 = SummaryEntity(
            id = "2",
            originalText = "Test text 2",
            shortSummary = "Short summary 2",
            mediumSummary = "Medium summary 2",
            detailedSummary = "Detailed summary 2",
            createdAt = System.currentTimeMillis() + 1000,
            isSaved = true
        )
        
        // When
        dao.insertSummary(summary1)
        dao.insertSummary(summary2)
        val savedSummaries = dao.getSavedSummaries().first()
        
        // Then
        assertEquals(1, savedSummaries.size)
        assertEquals(summary2.id, savedSummaries.first().id)
        assertTrue(savedSummaries.first().isSaved)
    }
    
    @Test
    fun searchSummaries() = runTest {
        // Given
        val summary1 = SummaryEntity(
            id = "1",
            originalText = "Apple is a fruit",
            shortSummary = "Apple short",
            mediumSummary = "Apple medium",
            detailedSummary = "Apple detailed",
            createdAt = System.currentTimeMillis(),
            isSaved = false
        )
        
        val summary2 = SummaryEntity(
            id = "2",
            originalText = "Banana is yellow",
            shortSummary = "Banana short",
            mediumSummary = "Banana medium",
            detailedSummary = "Banana detailed",
            createdAt = System.currentTimeMillis() + 1000,
            isSaved = false
        )
        
        // When
        dao.insertSummary(summary1)
        dao.insertSummary(summary2)
        val searchResults = dao.searchSummaries("Apple").first()
        
        // Then
        assertEquals(1, searchResults.size)
        assertEquals(summary1.id, searchResults.first().id)
    }
    
    @Test
    fun updateSaveStatus() = runTest {
        // Given
        val summary = SummaryEntity(
            id = "1",
            originalText = "Test text",
            shortSummary = "Short summary",
            mediumSummary = "Medium summary",
            detailedSummary = "Detailed summary",
            createdAt = System.currentTimeMillis(),
            isSaved = false
        )
        
        // When
        dao.insertSummary(summary)
        dao.updateSaveStatus("1", true)
        
        val savedSummaries = dao.getSavedSummaries().first()
        
        // Then
        assertEquals(1, savedSummaries.size)
        assertTrue(savedSummaries.first().isSaved)
    }
    
    @Test
    fun deleteSummary() = runTest {
        // Given
        val summary = SummaryEntity(
            id = "1",
            originalText = "Test text",
            shortSummary = "Short summary",
            mediumSummary = "Medium summary",
            detailedSummary = "Detailed summary",
            createdAt = System.currentTimeMillis(),
            isSaved = false
        )
        
        // When
        dao.insertSummary(summary)
        dao.deleteSummaryById("1")
        val summaries = dao.getAllSummaries().first()
        
        // Then
        assertEquals(0, summaries.size)
    }
}
