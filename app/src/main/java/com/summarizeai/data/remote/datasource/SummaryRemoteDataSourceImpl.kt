package com.summarizeai.data.remote.datasource

import com.summarizeai.data.model.ApiResult
import com.summarizeai.data.remote.api.SummarizerApi
import com.summarizeai.data.remote.api.SummarizeRequest
import com.summarizeai.data.remote.api.SummarizeResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryRemoteDataSourceImpl @Inject constructor(
    private val api: SummarizerApi
) : SummaryRemoteDataSource {
    
    override suspend fun summarizeText(request: SummarizeRequest): ApiResult<SummarizeResponse> {
        return try {
            val response = api.summarize(request)
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error("Network not available: ${e.message}")
        }
    }
}
