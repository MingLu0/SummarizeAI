package com.nutshell.domain.repository

import com.nutshell.data.model.ApiResult
import com.nutshell.data.model.StreamingResult
import com.nutshell.data.model.SummaryData
import com.nutshell.data.model.V4StreamingResult
import kotlinx.coroutines.flow.Flow

interface SummaryRepository {
    suspend fun summarizeText(text: String): ApiResult<SummaryData>
    fun summarizeTextStreaming(text: String): Flow<StreamingResult>

    /**
     * V4 API: Streaming summarization with structured fields
     * @param text Input text to summarize (optional if url is provided)
     * @param url URL to scrape and summarize (optional if text is provided)
     * @param style Summary style: "skimmer", "executive", or "eli5"
     */
    fun summarizeTextStreamingV4(
        text: String? = null,
        url: String? = null,
        style: String = "executive"
    ): Flow<V4StreamingResult>

    fun getAllSummaries(): Flow<List<SummaryData>>
    fun getSavedSummaries(): Flow<List<SummaryData>>
    suspend fun getLatestSummary(): SummaryData?
    suspend fun saveSummary(summaryData: SummaryData)
    suspend fun deleteSummary(id: String)
    suspend fun toggleSaveStatus(id: String)
    fun searchSummaries(query: String): Flow<List<SummaryData>>
}
