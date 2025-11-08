package com.nutshell.data.remote.datasource

import com.nutshell.data.model.ApiResult
import com.nutshell.data.remote.api.SummarizerApi
import com.nutshell.data.remote.api.SummarizeRequest
import com.nutshell.data.remote.api.SummarizeResponse
import com.nutshell.utils.NetworkUtils
import kotlinx.coroutines.delay
import java.net.SocketTimeoutException
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryRemoteDataSourceImpl @Inject constructor(
    private val api: SummarizerApi,
    private val networkUtils: NetworkUtils
) : SummaryRemoteDataSource {
    
    override suspend fun summarizeText(request: SummarizeRequest): ApiResult<SummarizeResponse> {
        // Check network connectivity first
        if (!networkUtils.isNetworkAvailable()) {
            println("SummaryRemoteDataSource: No network connection available")
            return ApiResult.Error("No network connection. Please check your internet connection and try again.")
        }
        
        val maxRetries = 3
        var lastException: Exception? = null
        
        for (attempt in 1..maxRetries) {
            try {
                println("SummaryRemoteDataSource: Making API request (attempt $attempt/$maxRetries) with text length: ${request.text.length}")
                val response = api.summarize(request)
                println("SummaryRemoteDataSource: API request successful, response: $response")
                return ApiResult.Success(response)
            } catch (e: Exception) {
                lastException = e
                println("SummaryRemoteDataSource: API request failed (attempt $attempt/$maxRetries): ${e.javaClass.simpleName}: ${e.message}")
                e.printStackTrace()
                
                // Don't retry on certain errors
                if (e is UnknownHostException || e is ConnectException) {
                    return ApiResult.Error(getUserFriendlyErrorMessage(e))
                }
                
                // If this is the last attempt, don't wait
                if (attempt < maxRetries) {
                    val delayMs = calculateRetryDelay(attempt)
                    println("SummaryRemoteDataSource: Retrying in ${delayMs}ms...")
                    delay(delayMs)
                }
            }
        }
        
        // All retries failed
        return ApiResult.Error(getUserFriendlyErrorMessage(lastException))
    }
    
    private fun calculateRetryDelay(attempt: Int): Long {
        // Exponential backoff: 1s, 2s, 4s
        return (1000L * (1 shl (attempt - 1))).coerceAtMost(5000L)
    }
    
    private fun getUserFriendlyErrorMessage(exception: Exception?): String {
        return when (exception) {
            is SocketTimeoutException -> "Request timed out. The AI service is taking longer than expected. Please try again."
            is ConnectException -> "Cannot connect to AI service. Please check your network connection and ensure the service is running."
            is UnknownHostException -> "Cannot reach AI service. Please check your network connection."
            else -> "Network error occurred. Please check your connection and try again."
        }
    }
}
