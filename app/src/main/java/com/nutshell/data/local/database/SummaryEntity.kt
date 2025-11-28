package com.nutshell.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "summaries")
data class SummaryEntity(
    @PrimaryKey
    val id: String,
    val originalText: String,
    val shortSummary: String,
    val mediumSummary: String,
    val detailedSummary: String,
    val createdAt: Long,
    val isSaved: Boolean = false,

    // V4 API structured fields (nullable for backward compatibility)
    val title: String? = null,
    val mainSummary: String? = null,
    val keyPoints: String? = null,      // JSON array string: ["point1", "point2", ...]
    val category: String? = null,
    val sentiment: String? = null,
    val readTimeMin: Int? = null
)
