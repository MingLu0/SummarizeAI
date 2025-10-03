package com.summarizeai.data.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SummaryDao {
    
    @Query("SELECT * FROM summaries ORDER BY createdAt DESC")
    fun getAllSummaries(): Flow<List<SummaryEntity>>
    
    @Query("SELECT * FROM summaries WHERE isSaved = 1 ORDER BY createdAt DESC")
    fun getSavedSummaries(): Flow<List<SummaryEntity>>
    
    @Query("SELECT * FROM summaries WHERE originalText LIKE '%' || :query || '%' OR shortSummary LIKE '%' || :query || '%' OR mediumSummary LIKE '%' || :query || '%' OR detailedSummary LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchSummaries(query: String): Flow<List<SummaryEntity>>
    
    @Query("SELECT * FROM summaries WHERE isSaved = 1 AND (originalText LIKE '%' || :query || '%' OR shortSummary LIKE '%' || :query || '%' OR mediumSummary LIKE '%' || :query || '%' OR detailedSummary LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    fun searchSavedSummaries(query: String): Flow<List<SummaryEntity>>
    
    @Query("SELECT * FROM summaries WHERE id = :id")
    suspend fun getSummaryById(id: String): SummaryEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: SummaryEntity)
    
    @Update
    suspend fun updateSummary(summary: SummaryEntity)
    
    @Query("UPDATE summaries SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSaveStatus(id: String, isSaved: Boolean)
    
    @Delete
    suspend fun deleteSummary(summary: SummaryEntity)
    
    @Query("DELETE FROM summaries WHERE id = :id")
    suspend fun deleteSummaryById(id: String)
    
    @Query("DELETE FROM summaries")
    suspend fun deleteAllSummaries()
}
