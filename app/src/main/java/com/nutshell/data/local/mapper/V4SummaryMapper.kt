package com.nutshell.data.local.mapper

import com.nutshell.data.local.database.SummaryEntity
import com.nutshell.data.model.V4SummaryData
import org.json.JSONArray
import org.json.JSONException
import java.util.Date

/**
 * Converts V4SummaryData to SummaryEntity for Room database storage
 */
fun V4SummaryData.toSummaryEntity(): SummaryEntity {
    return SummaryEntity(
        id = id,
        originalText = originalText,
        shortSummary = shortSummary,
        mediumSummary = mediumSummary,
        detailedSummary = detailedSummary,
        createdAt = createdAt.time,
        isSaved = isSaved,
        // V4 fields
        title = title,
        mainSummary = mainSummary,
        keyPoints = keyPoints.toJsonString(),
        category = category,
        sentiment = sentiment,
        readTimeMin = readTimeMin
    )
}

/**
 * Converts SummaryEntity to V4SummaryData
 */
fun SummaryEntity.toV4SummaryData(): V4SummaryData {
    return V4SummaryData(
        id = id,
        originalText = originalText,
        shortSummary = shortSummary,
        mediumSummary = mediumSummary,
        detailedSummary = detailedSummary,
        createdAt = Date(createdAt),
        isSaved = isSaved,
        // V4 fields
        title = title,
        mainSummary = mainSummary,
        keyPoints = keyPoints?.parseJsonArray() ?: emptyList(),
        category = category,
        sentiment = sentiment,
        readTimeMin = readTimeMin
    )
}

/**
 * Converts List<String> to JSON array string for database storage
 */
private fun List<String>.toJsonString(): String? {
    if (isEmpty()) return null
    return JSONArray(this).toString()
}

/**
 * Parses JSON array string back to List<String>
 */
private fun String.parseJsonArray(): List<String> {
    return try {
        val jsonArray = JSONArray(this)
        List(jsonArray.length()) { i ->
            jsonArray.getString(i)
        }
    } catch (e: JSONException) {
        emptyList()
    }
}
