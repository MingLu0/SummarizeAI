package com.summarizeai.data.remote.api

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.utils.io.*
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
            println("StreamingSummarizerService: Starting SSE connection to $baseUrl/api/v1/summarize/stream")
            
            val requestBody = mapOf(
                "text" to text,
                "max_tokens" to 1024,
                "prompt" to "Summarize the following text concisely:"
            )
            
            client.preparePost("$baseUrl/api/v1/summarize/stream") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.execute { response ->
                println("StreamingSummarizerService: Response status: ${response.status}")
                
                val channel: ByteReadChannel = response.bodyAsChannel()
                
                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: continue
                    
                    // SSE format: "data: {json}"
                    if (line.startsWith("data: ")) {
                        val jsonData = line.substring(6) // Remove "data: " prefix
                        
                        try {
                            val json = JSONObject(jsonData)
                            
                            // Check for error field first (backend sends errors as SSE events)
                            if (json.has("error")) {
                                println("StreamingSummarizerService: Error from backend: ${json.getString("error")}")
                                throw Exception(json.getString("error"))
                            }
                            
                            val content = json.getString("content")
                            val done = json.getBoolean("done")
                            
                            println("StreamingSummarizerService: Received chunk - content: ${content.take(50)}..., done: $done")
                            emit(StreamChunk(content, done))
                            
                            if (done) {
                                println("StreamingSummarizerService: Stream completed")
                                break
                            }
                        } catch (e: Exception) {
                            println("StreamingSummarizerService: Error parsing SSE chunk: ${e.message}")
                            // Continue reading even if one chunk fails
                        }
                    }
                }
            }
            
            println("StreamingSummarizerService: SSE connection closed")
        } catch (e: Exception) {
            println("StreamingSummarizerService: Connection failed: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}
