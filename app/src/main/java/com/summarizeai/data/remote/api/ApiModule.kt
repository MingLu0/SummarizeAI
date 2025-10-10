package com.summarizeai.data.remote.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

private const val BASE_URL = "https://colin730-summarizerapp.hf.space/"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)  // Increased for AI processing
            .readTimeout(120, TimeUnit.SECONDS)    // Increased for large text processing
            .writeTimeout(60, TimeUnit.SECONDS)    // Increased for large requests
            .retryOnConnectionFailure(true)        // Enable automatic retry
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideSummarizerApi(retrofit: Retrofit): SummarizerApi {
        return retrofit.create(SummarizerApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
    
    @Provides
    @Singleton
    fun provideWebContentRepository(
        webContentRepositoryImpl: com.summarizeai.data.remote.repository.WebContentRepositoryImpl
    ): com.summarizeai.domain.repository.WebContentRepository {
        return webContentRepositoryImpl
    }
}
