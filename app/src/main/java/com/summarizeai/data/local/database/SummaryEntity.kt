package com.summarizeai.data.local.database

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
    val isSaved: Boolean = false
)
