package com.nutshell.data.remote.api

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.utils.io.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamingSummarizerService @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    
    companion object {
        private const val TAG = "StreamingSummarizerSvc"
    }
    
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            gson()
        }
        engine {
            preconfigured = okHttpClient
        }
    }
    
    fun streamSummary(text: String, baseUrl: String): Flow<StreamChunk> = flow {
        try {
            Log.d(TAG, "streamSummary: Starting SSE connection to $baseUrl/api/v2/summarize/stream")
            Log.d(TAG, "streamSummary: Input text length: ${text.length} characters")
            
            val requestBody = mapOf(
                "url" to text,
                "max_tokens" to 256,
                "prompt" to "Summarize the following text concisely:"
            )
            
            client.preparePost("$baseUrl/api/v3/scrape-and-summarize/stream") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.execute { response ->
                Log.d(TAG, "streamSummary: Response status: ${response.status}")
                
                val channel: ByteReadChannel = response.bodyAsChannel()
                var chunkCount = 0
                
                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: continue
                    
                    // SSE format: "data: {json}"
                    if (line.startsWith("data: ")) {
                        val jsonData = line.substring(6) // Remove "data: " prefix
                        
                        try {
                            val json = JSONObject(jsonData)
                            
                            // Check for error field first (backend sends errors as SSE events)
                            if (json.has("error")) {
                                val errorMsg = json.getString("error")
                                Log.e(TAG, "streamSummary: Error from backend: $errorMsg")
                                throw Exception(errorMsg)
                            }
                            
                            val content = json.getString("content")
                            val done = json.getBoolean("done")
                            
                            chunkCount++
                            Log.d(TAG, "streamSummary: Received chunk #$chunkCount from server")
                            Log.d(TAG, "streamSummary: Chunk content length: ${content.length} characters")
                            Log.d(TAG, "streamSummary: Chunk content preview: ${content.take(50)}")
                            Log.d(TAG, "streamSummary: Chunk done status: $done")
                            
                            emit(StreamChunk(content, done))
                            Log.d(TAG, "streamSummary: Emitted chunk #$chunkCount to Flow")
                            
                            // Add delay to allow UI to recompose between chunks
                            // This prevents all chunks from being batched together in a single frame
                            if (!done) {
                                delay(30) // 30ms delay between chunks for smooth streaming effect
                            }
                            
                            if (done) {
                                Log.d(TAG, "streamSummary: Stream completed. Total chunks: $chunkCount")
                                break
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "streamSummary: Error parsing SSE chunk: ${e.message}")
                            // Continue reading even if one chunk fails
                        }
                    }
                }
            }
            
            Log.d(TAG, "streamSummary: SSE connection closed")
        } catch (e: Exception) {
            Log.e(TAG, "streamSummary: Connection failed: ${e.message}")
            Log.e(TAG, "streamSummary: Exception type: ${e.javaClass.simpleName}")
            e.printStackTrace()
            throw e
        }
    }
}
