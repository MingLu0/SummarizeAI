package com.summarizeai.data.remote.datasource

import com.summarizeai.data.model.ApiResult
import com.summarizeai.data.remote.api.SummarizeRequest
import com.summarizeai.data.remote.api.SummarizeResponse

interface SummaryRemoteDataSource {
    suspend fun summarizeText(request: SummarizeRequest): ApiResult<SummarizeResponse>
}
