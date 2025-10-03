package com.summarizeai.di

import com.summarizeai.data.remote.datasource.SummaryRemoteDataSource
import com.summarizeai.data.remote.datasource.SummaryRemoteDataSourceImpl
import com.summarizeai.data.repository.SummaryRepositoryImpl
import com.summarizeai.domain.repository.SummaryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindSummaryRepository(
        summaryRepositoryImpl: SummaryRepositoryImpl
    ): SummaryRepository
    
    @Binds
    @Singleton
    abstract fun bindSummaryRemoteDataSource(
        summaryRemoteDataSourceImpl: SummaryRemoteDataSourceImpl
    ): SummaryRemoteDataSource
}
