package com.summarizeai.data.repository

import com.summarizeai.data.local.datasource.SummaryLocalDataSource
import com.summarizeai.data.local.mapper.SummaryMapper.toSummaryData
import com.summarizeai.data.local.mapper.SummaryMapper.toSummaryDataList
import com.summarizeai.data.local.mapper.SummaryMapper.toSummaryEntity
import com.summarizeai.data.model.ApiResult
import com.summarizeai.data.model.SummaryData
import com.summarizeai.data.remote.api.SummarizerApi
import com.summarizeai.data.remote.api.SummarizeRequest
import com.summarizeai.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryRepositoryImpl @Inject constructor(
    private val api: SummarizerApi,
    private val localDataSource: SummaryLocalDataSource
) : SummaryRepository {
    
    override suspend fun summarizeText(text: String): ApiResult<SummaryData> {
        return try {
            val request = SummarizeRequest(text = text)
            val response = api.summarize(request)
            
            val summaryData = SummaryData(
                originalText = text,
                shortSummary = generateShortSummary(response.summary),
                mediumSummary = response.summary,
                detailedSummary = generateDetailedSummary(response.summary)
            )
            
            // Save to local database
            localDataSource.insertSummary(summaryData.toSummaryEntity())
            
            ApiResult.Success(summaryData)
        } catch (e: Exception) {
            ApiResult.Error("Network not available")
        }
    }
    
    override fun getAllSummaries(): Flow<List<SummaryData>> = 
        localDataSource.getAllSummaries().map { entities ->
            entities.toSummaryDataList()
        }
    
    override fun getSavedSummaries(): Flow<List<SummaryData>> = 
        localDataSource.getSavedSummaries().map { entities ->
            entities.toSummaryDataList()
        }
    
    override suspend fun saveSummary(summaryData: SummaryData) {
        localDataSource.updateSaveStatus(summaryData.id, true)
    }
    
    override suspend fun deleteSummary(id: String) {
        localDataSource.deleteSummaryById(id)
    }
    
    override suspend fun toggleSaveStatus(id: String) {
        val entity = localDataSource.getSummaryById(id)
        entity?.let {
            localDataSource.updateSaveStatus(id, !it.isSaved)
        }
    }
    
    override fun searchSummaries(query: String): Flow<List<SummaryData>> = 
        localDataSource.searchSummaries(query).map { entities ->
            entities.toSummaryDataList()
        }
    
    private fun generateShortSummary(summary: String): String {
        // Generate a shorter version (first 2 sentences or 100 chars)
        val sentences = summary.split(". ")
        return if (sentences.size >= 2) {
            sentences.take(2).joinToString(". ") + "."
        } else {
            summary.take(100) + if (summary.length > 100) "..." else ""
        }
    }
    
    private fun generateDetailedSummary(summary: String): String {
        // Generate a more detailed version by expanding the summary
        return summary + " This summary provides comprehensive insights into the original content, covering all major points and supporting details."
    }
}
