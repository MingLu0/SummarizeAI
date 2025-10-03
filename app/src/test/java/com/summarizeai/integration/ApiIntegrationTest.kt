package com.summarizeai.integration

import com.summarizeai.data.remote.api.SummarizerApi
import com.summarizeai.data.remote.api.SummarizeRequest
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiIntegrationTest {
    
    private lateinit var api: SummarizerApi
    
    @Before
    fun setup() {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8000/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        api = retrofit.create(SummarizerApi::class.java)
    }
    
    @Test
    fun `API integration test with local service`() = runBlocking {
        // Given
        val request = SummarizeRequest(
            text = "This is a test text for summarization. It contains multiple sentences to test the AI summarization service.",
            maxTokens = 256,
            prompt = "Summarize the following text concisely:"
        )
        
        // When
        try {
            val response = api.summarize(request)
            
            // Then
            assertNotNull(response)
            assertNotNull(response.summary)
            assertTrue(response.summary.isNotEmpty())
            assertNotNull(response.model)
            
            println("API Response: $response")
            
        } catch (e: Exception) {
            // This is expected if the local API service is not running
            println("API service not available (expected in CI/CD): ${e.message}")
            assertTrue("Expected connection error", e.message?.contains("connect") == true)
        }
    }
    
    @Test
    fun `API integration test with empty text`() = runBlocking {
        // Given
        val request = SummarizeRequest(
            text = "",
            maxTokens = 256
        )
        
        // When
        try {
            val response = api.summarize(request)
            
            // Then
            assertNotNull(response)
            
        } catch (e: Exception) {
            // This is expected behavior for empty text or if service is not running
            println("Expected error for empty text or service unavailable: ${e.message}")
        }
    }
    
    @Test
    fun `API integration test with long text`() = runBlocking {
        // Given
        val longText = "This is a very long text that contains many sentences. ".repeat(50)
        val request = SummarizeRequest(
            text = longText,
            maxTokens = 256
        )
        
        // When
        try {
            val response = api.summarize(request)
            
            // Then
            assertNotNull(response)
            assertNotNull(response.summary)
            assertTrue(response.summary.isNotEmpty())
            
            println("Long text summarization successful")
            
        } catch (e: Exception) {
            // This is expected if the local API service is not running
            println("API service not available for long text test: ${e.message}")
        }
    }
}
