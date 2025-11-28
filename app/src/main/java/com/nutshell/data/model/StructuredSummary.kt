package com.nutshell.data.model

/**
 * V4 API structured summary response.
 * Represents the complete state of a structured summary as it builds up during streaming.
 */
data class StructuredSummary(
    val title: String? = null,              // 6-10 words, concise title
    val mainSummary: String? = null,        // 2 sentences maximum (renamed from main_summary)
    val keyPoints: List<String> = emptyList(), // 3-5 items, each 8-12 words
    val category: String? = null,           // 1-2 words (e.g., "Tech", "Crime")
    val sentiment: String? = null,          // "positive", "negative", or "neutral"
    val readTimeMin: Int? = null            // Estimated reading time in minutes
)
