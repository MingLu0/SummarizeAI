package com.nutshell.data.model

/**
 * V4 API streaming events from Server-Sent Events (SSE) stream.
 */
sealed class V4StreamEvent {
    /**
     * Metadata event (first event if include_metadata=true)
     */
    data class Metadata(
        val inputType: String,           // "url" or "text"
        val url: String? = null,         // Only if input_type is "url"
        val title: String? = null,       // Only if input_type is "url"
        val author: String? = null,      // Only if input_type is "url"
        val date: String? = null,        // Only if input_type is "url"
        val siteName: String? = null,    // Only if input_type is "url"
        val scrapeMethod: String? = null, // Only if input_type is "url"
        val scrapeLatencyMs: Double? = null, // Only if input_type is "url"
        val extractedTextLength: Int? = null, // Only if input_type is "url"
        val textLength: Int? = null,     // Only if input_type is "text"
        val style: String                // "skimmer", "executive", or "eli5"
    ) : V4StreamEvent()

    /**
     * Patch event (streaming update to structured summary)
     */
    data class Patch(
        val delta: PatchOperation?,      // The change operation
        val state: StructuredSummary,    // Current complete state after applying patch
        val done: Boolean,               // true when generation is complete
        val tokensUsed: Int,             // Number of tokens generated so far
        val latencyMs: Double? = null    // Total generation time (only on final event)
    ) : V4StreamEvent()

    /**
     * Error event
     */
    data class Error(
        val message: String,
        val done: Boolean = true,
        val tokensUsed: Int = 0
    ) : V4StreamEvent()
}
