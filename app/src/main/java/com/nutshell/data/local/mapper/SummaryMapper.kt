package com.nutshell.data.local.mapper

import com.nutshell.data.local.database.SummaryEntity
import com.nutshell.data.model.SummaryData
import java.util.*

object SummaryMapper {

    fun SummaryEntity.toSummaryData(): SummaryData {
        return SummaryData(
            id = this.id,
            originalText = this.originalText,
            shortSummary = this.shortSummary,
            mediumSummary = this.mediumSummary,
            detailedSummary = this.detailedSummary,
            createdAt = Date(this.createdAt),
            isSaved = this.isSaved,
        )
    }

    fun SummaryData.toSummaryEntity(): SummaryEntity {
        return SummaryEntity(
            id = this.id,
            originalText = this.originalText,
            shortSummary = this.shortSummary,
            mediumSummary = this.mediumSummary,
            detailedSummary = this.detailedSummary,
            createdAt = this.createdAt.time,
            isSaved = this.isSaved,
        )
    }

    fun List<SummaryEntity>.toSummaryDataList(): List<SummaryData> {
        return this.map { it.toSummaryData() }
    }
}
