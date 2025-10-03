package com.summarizeai.di

import android.content.Context
import androidx.room.Room
import com.summarizeai.data.local.database.SummarizeDatabase
import com.summarizeai.data.local.database.SummaryDao
import com.summarizeai.data.local.datasource.SummaryLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SummarizeDatabase {
        return Room.databaseBuilder(
            context,
            SummarizeDatabase::class.java,
            SummarizeDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    fun provideSummaryDao(database: SummarizeDatabase): SummaryDao {
        return database.summaryDao()
    }
    
    @Provides
    @Singleton
    fun provideSummaryLocalDataSource(summaryDao: SummaryDao): SummaryLocalDataSource {
        return SummaryLocalDataSource(summaryDao)
    }
}
