package com.nutshell.data.remote.datasource

import com.nutshell.data.model.ApiResult
import com.nutshell.data.remote.api.SummarizeRequest
import com.nutshell.data.remote.api.SummarizeResponse

interface SummaryRemoteDataSource {
    suspend fun summarizeText(request: SummarizeRequest): ApiResult<SummarizeResponse>
}
