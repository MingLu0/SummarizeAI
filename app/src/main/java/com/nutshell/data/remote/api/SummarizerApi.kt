package com.nutshell.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST

interface SummarizerApi {
    @POST("api/v1/summarize/")
    suspend fun summarize(@Body request: SummarizeRequest): SummarizeResponse
}

data class SummarizeRequest(
    val text: String,
    val max_tokens: Int = 256,
    val prompt: String = "Summarize the following text concisely:",
)

data class WebSummarizeRequest(
    val text: String,
    val max_tokens: Int = 1024, // Longer token limit for web content
    val prompt: String = "Summarize the following web content concisely:",
)

data class SummarizeResponse(
    val summary: String,
    val model: String,
    val tokens_used: Int?,
    val latency_ms: Double?,
)
