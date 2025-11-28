package com.nutshell.data.model

/**
 * Sealed class representing V4 streaming API results.
 * Used by repository to communicate streaming state to ViewModel.
 */
sealed class V4StreamingResult {
    /**
     * Metadata event received (first event with scraping info)
     */
    data class Metadata(
        val inputType: String,
        val url: String? = null,
        val title: String? = null,
        val style: String
    ) : V4StreamingResult()

    /**
     * Progress update with partial structured summary
     */
    data class Progress(
        val structuredSummary: StructuredSummary,
        val tokensUsed: Int
    ) : V4StreamingResult()

    /**
     * Streaming completed successfully with final summary
     */
    data class Complete(
        val summaryData: V4SummaryData,
        val tokensUsed: Int,
        val latencyMs: Double?
    ) : V4StreamingResult()

    /**
     * Error occurred during streaming
     */
    data class Error(val message: String) : V4StreamingResult()
}
