package com.nutshell.data.local.datasource

import com.nutshell.data.local.database.SummaryDao
import com.nutshell.data.local.database.SummaryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryLocalDataSource @Inject constructor(
    private val summaryDao: SummaryDao
) {
    
    fun getAllSummaries(): Flow<List<SummaryEntity>> = summaryDao.getAllSummaries()
    
    fun getSavedSummaries(): Flow<List<SummaryEntity>> = summaryDao.getSavedSummaries()
    
    fun searchSummaries(query: String): Flow<List<SummaryEntity>> = 
        summaryDao.searchSummaries(query)
    
    fun searchSavedSummaries(query: String): Flow<List<SummaryEntity>> = 
        summaryDao.searchSavedSummaries(query)
    
    suspend fun insertSummary(summary: SummaryEntity) = summaryDao.insertSummary(summary)
    
    suspend fun updateSummary(summary: SummaryEntity) = summaryDao.updateSummary(summary)
    
    suspend fun updateSaveStatus(id: String, isSaved: Boolean) = 
        summaryDao.updateSaveStatus(id, isSaved)
    
    suspend fun deleteSummaryById(id: String) = summaryDao.deleteSummaryById(id)
    
    suspend fun deleteAllSummaries() = summaryDao.deleteAllSummaries()
    
    suspend fun getSummaryById(id: String) = summaryDao.getSummaryById(id)
    
    suspend fun getLatestSummary() = summaryDao.getLatestSummary()
}
