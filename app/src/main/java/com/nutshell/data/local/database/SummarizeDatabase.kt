package com.nutshell.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [SummaryEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class SummarizeDatabase : RoomDatabase() {

    abstract fun summaryDao(): SummaryDao

    companion object {
        const val DATABASE_NAME = "summarize_database"

        /**
         * Migration from version 1 to 2: Add V4 API structured fields
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add V4 structured fields as nullable columns
                database.execSQL("ALTER TABLE summaries ADD COLUMN title TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE summaries ADD COLUMN mainSummary TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE summaries ADD COLUMN keyPoints TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE summaries ADD COLUMN category TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE summaries ADD COLUMN sentiment TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE summaries ADD COLUMN readTimeMin INTEGER DEFAULT NULL")
            }
        }
    }
}
