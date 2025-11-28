package com.nutshell.data.model

import java.util.Date
import java.util.UUID

/**
 * V4 Summary data model with structured fields.
 * Maintains backward compatibility with V3 SummaryData while adding V4 fields.
 */
data class V4SummaryData(
    val id: String = UUID.randomUUID().toString(),
    val originalText: String,           // Original input text (for V3 compatibility)

    // V4 Structured fields
    val title: String? = null,
    val mainSummary: String? = null,
    val keyPoints: List<String> = emptyList(),
    val category: String? = null,
    val sentiment: String? = null,
    val readTimeMin: Int? = null,

    // V3 Compatibility fields (generated from V4 fields if needed)
    val shortSummary: String = "",      // First key point or truncated main summary
    val mediumSummary: String = "",     // Main summary + key points formatted
    val detailedSummary: String = "",   // Full formatted text with all fields

    val createdAt: Date = Date(),
    val isSaved: Boolean = false
) {
    companion object {
        /**
         * Creates V4SummaryData from StructuredSummary and original text.
         * Generates V3-compatible summary fields from V4 structured data.
         */
        fun fromStructuredSummary(
            structuredSummary: StructuredSummary,
            originalText: String,
            id: String = UUID.randomUUID().toString()
        ): V4SummaryData {
            // Generate formatted text for V3 compatibility
            val formattedText = buildFormattedText(structuredSummary)

            return V4SummaryData(
                id = id,
                originalText = originalText,
                title = structuredSummary.title,
                mainSummary = structuredSummary.mainSummary,
                keyPoints = structuredSummary.keyPoints,
                category = structuredSummary.category,
                sentiment = structuredSummary.sentiment,
                readTimeMin = structuredSummary.readTimeMin,
                shortSummary = generateShortSummary(structuredSummary),
                mediumSummary = formattedText,
                detailedSummary = formattedText
            )
        }

        /**
         * Builds formatted text from structured summary for display.
         */
        private fun buildFormattedText(summary: StructuredSummary): String {
            return buildString {
                summary.title?.let {
                    appendLine(it)
                    appendLine()
                }

                summary.mainSummary?.let {
                    appendLine(it)
                    appendLine()
                }

                if (summary.keyPoints.isNotEmpty()) {
                    appendLine("Key Points:")
                    summary.keyPoints.forEach { point ->
                        appendLine("• $point")
                    }
                    appendLine()
                }

                // Metadata line
                val metadata = buildList {
                    summary.category?.let { add("Category: $it") }
                    summary.sentiment?.let { add("Sentiment: $it") }
                    summary.readTimeMin?.let { add("Read Time: $it min") }
                }
                if (metadata.isNotEmpty()) {
                    append(metadata.joinToString(" | "))
                }
            }.trim()
        }

        /**
         * Generates short summary (first key point or truncated main summary)
         */
        private fun generateShortSummary(summary: StructuredSummary): String {
            return summary.keyPoints.firstOrNull()
                ?: summary.mainSummary?.take(100)
                ?: summary.title
                ?: ""
        }
    }
}
