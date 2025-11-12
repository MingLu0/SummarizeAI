package com.nutshell.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SummaryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class SummarizeDatabase : RoomDatabase() {

    abstract fun summaryDao(): SummaryDao

    companion object {
        const val DATABASE_NAME = "summarize_database"
    }
}
