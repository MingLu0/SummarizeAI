package com.nutshell.data.model

import java.util.*

data class SummaryData(
    val id: String = UUID.randomUUID().toString(),
    val originalText: String,
    val shortSummary: String,
    val mediumSummary: String,
    val detailedSummary: String,
    val createdAt: Date = Date(),
    val isSaved: Boolean = false
)

data class SummaryRequest(
    val text: String,
    val maxTokens: Int = 256,
    val prompt: String = "Summarize the following text concisely:"
)

data class SummaryResponse(
    val summary: String,
    val model: String,
    val tokensUsed: Int?,
    val latencyMs: Double?
)

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}
