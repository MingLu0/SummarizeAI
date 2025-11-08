package com.nutshell.domain.repository

import com.nutshell.data.model.ApiResult
import com.nutshell.data.model.SummaryData
import com.nutshell.data.model.StreamingResult
import kotlinx.coroutines.flow.Flow

interface SummaryRepository {
    suspend fun summarizeText(text: String): ApiResult<SummaryData>
    fun summarizeTextStreaming(text: String): Flow<StreamingResult>
    fun getAllSummaries(): Flow<List<SummaryData>>
    fun getSavedSummaries(): Flow<List<SummaryData>>
    suspend fun getLatestSummary(): SummaryData?
    suspend fun saveSummary(summaryData: SummaryData)
    suspend fun deleteSummary(id: String)
    suspend fun toggleSaveStatus(id: String)
    fun searchSummaries(query: String): Flow<List<SummaryData>>
}
