package com.nutshell.data.model

/**
 * V4 API patch operations for incremental updates to structured summary.
 */
sealed class PatchOperation {
    /**
     * Set or overwrite a scalar field (title, mainSummary, category, sentiment, readTimeMin)
     */
    data class Set(
        val field: String,
        val value: Any?  // String, Int, or null
    ) : PatchOperation()

    /**
     * Append an item to the keyPoints array
     */
    data class Append(
        val field: String,  // Should be "key_points"
        val value: String
    ) : PatchOperation()

    /**
     * Signal that generation is complete
     */
    data object Done : PatchOperation()
}
