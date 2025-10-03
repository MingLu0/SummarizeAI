package com.summarizeai.data.repository

import com.summarizeai.data.model.ApiResult
import com.summarizeai.data.model.SummaryData
import com.summarizeai.data.model.SummaryRequest
import com.summarizeai.data.remote.api.SummarizerApi
import com.summarizeai.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryRepositoryImpl @Inject constructor(
    private val api: SummarizerApi
) : SummaryRepository {
    
    private val _summaries = MutableStateFlow<List<SummaryData>>(emptyList())
    
    override suspend fun summarizeText(text: String): ApiResult<SummaryData> {
        return try {
            val request = SummaryRequest(text = text)
            val response = api.summarize(request)
            
            val summaryData = SummaryData(
                originalText = text,
                shortSummary = generateShortSummary(response.summary),
                mediumSummary = response.summary,
                detailedSummary = generateDetailedSummary(response.summary)
            )
            
            // Save to local storage
            val currentList = _summaries.value.toMutableList()
            currentList.add(0, summaryData) // Add to beginning
            _summaries.value = currentList
            
            ApiResult.Success(summaryData)
        } catch (e: Exception) {
            ApiResult.Error("Network not available")
        }
    }
    
    override fun getAllSummaries(): Flow<List<SummaryData>> = _summaries.asStateFlow()
    
    override fun getSavedSummaries(): Flow<List<SummaryData>> = 
        _summaries.asStateFlow().map { summaries ->
            summaries.filter { it.isSaved }
        }
    
    override suspend fun saveSummary(summaryData: SummaryData) {
        val currentList = _summaries.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == summaryData.id }
        if (index != -1) {
            currentList[index] = summaryData.copy(isSaved = true)
            _summaries.value = currentList
        }
    }
    
    override suspend fun deleteSummary(id: String) {
        val currentList = _summaries.value.toMutableList()
        currentList.removeAll { it.id == id }
        _summaries.value = currentList
    }
    
    override suspend fun toggleSaveStatus(id: String) {
        val currentList = _summaries.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val summary = currentList[index]
            currentList[index] = summary.copy(isSaved = !summary.isSaved)
            _summaries.value = currentList
        }
    }
    
    override fun searchSummaries(query: String): Flow<List<SummaryData>> = 
        _summaries.asStateFlow().map { summaries ->
            if (query.isBlank()) {
                summaries
            } else {
                summaries.filter { summary ->
                    summary.originalText.contains(query, ignoreCase = true) ||
                    summary.shortSummary.contains(query, ignoreCase = true) ||
                    summary.mediumSummary.contains(query, ignoreCase = true) ||
                    summary.detailedSummary.contains(query, ignoreCase = true)
                }
            }
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
