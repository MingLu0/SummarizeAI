package com.summarizeai.domain.repository

import com.summarizeai.data.model.ApiResult
import com.summarizeai.data.model.SummaryData
import kotlinx.coroutines.flow.Flow

interface SummaryRepository {
    suspend fun summarizeText(text: String): ApiResult<SummaryData>
    fun getAllSummaries(): Flow<List<SummaryData>>
    fun getSavedSummaries(): Flow<List<SummaryData>>
    suspend fun saveSummary(summaryData: SummaryData)
    suspend fun deleteSummary(id: String)
    suspend fun toggleSaveStatus(id: String)
    fun searchSummaries(query: String): Flow<List<SummaryData>>
}
