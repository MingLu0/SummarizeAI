package com.nutshell.data.remote.api

import android.util.Log
import com.nutshell.data.model.PatchOperation
import com.nutshell.data.model.StructuredSummary
import com.nutshell.data.model.V4StreamEvent
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
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class V4StreamingSummarizerService @Inject constructor(
    private val okHttpClient: OkHttpClient,
) {

    companion object {
        private const val TAG = "V4StreamingSummarizerSvc"
        private const val BASE_URL = "https://colin730-summarizerapp.hf.space"
        private const val ENDPOINT = "/api/v4/scrape-and-summarize/stream-ndjson"
    }

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            gson()
        }
        engine {
            preconfigured = okHttpClient
        }
    }

    /**
     * Streams V4 API summary with structured fields.
     * @param text Input text to summarize (optional if url is provided)
     * @param url URL to scrape and summarize (optional if text is provided)
     * @param style Summary style: "skimmer", "executive", or "eli5"
     * @param maxTokens Token limit (128-2048, default 256)
     * @param includeMetadata Include metadata event (default true)
     * @return Flow of V4StreamEvent (Metadata, Patch, Error)
     */
    fun streamSummaryV4(
        text: String? = null,
        url: String? = null,
        style: String = "executive",
        maxTokens: Int = 256,
        includeMetadata: Boolean = true
    ): Flow<V4StreamEvent> = flow {
        require(text != null || url != null) { "Either text or url must be provided" }
        require(text == null || url == null) { "Cannot provide both text and url" }

        try {
            Log.d(TAG, "streamSummaryV4: Starting V4 SSE connection")
            Log.d(TAG, "streamSummaryV4: Style: $style, maxTokens: $maxTokens")

            if (text != null) {
                Log.d(TAG, "streamSummaryV4: Input text length: ${text.length} characters")
            } else {
                Log.d(TAG, "streamSummaryV4: Input URL: $url")
            }

            val requestBody = buildMap {
                text?.let { put("text", it) }
                url?.let { put("url", it) }
                put("style", style)
                put("max_tokens", maxTokens)
                put("include_metadata", includeMetadata)
                put("use_cache", true)
            }

            client.preparePost("$BASE_URL$ENDPOINT") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.execute { response ->
                Log.d(TAG, "streamSummaryV4: Response status: ${response.status}")

                val channel: ByteReadChannel = response.bodyAsChannel()
                var currentState = StructuredSummary()
                var eventCount = 0

                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: continue

                    // NDJSON SSE format: "data: {json}\n\n"
                    if (line.startsWith("data: ")) {
                        val jsonData = line.substring(6) // Remove "data: " prefix

                        try {
                            val json = JSONObject(jsonData)
                            eventCount++

                            // Check event type
                            when {
                                // Metadata event
                                json.has("type") && json.getString("type") == "metadata" -> {
                                    Log.d(TAG, "streamSummaryV4: Received metadata event")
                                    val event = parseMetadataEvent(json)
                                    emit(event)
                                }

                                // Patch event or final event
                                json.has("state") -> {
                                    Log.d(TAG, "streamSummaryV4: Received event #$eventCount")

                                    // Check for error first
                                    if (json.has("error")) {
                                        val errorMsg = json.getString("error")
                                        Log.e(TAG, "streamSummaryV4: Error from backend: $errorMsg")
                                        emit(V4StreamEvent.Error(errorMsg))
                                        break
                                    }

                                    // Parse patch event
                                    val patchEvent = parsePatchEvent(json)
                                    currentState = patchEvent.state

                                    Log.d(TAG, "streamSummaryV4: Event done=${patchEvent.done}, tokens=${patchEvent.tokensUsed}")
                                    emit(patchEvent)

                                    // Add delay for smooth UI animation
                                    if (!patchEvent.done) {
                                        delay(30) // 30ms delay between chunks
                                    }

                                    if (patchEvent.done) {
                                        Log.d(TAG, "streamSummaryV4: Stream completed. Total events: $eventCount")
                                        break
                                    }
                                }

                                // Error event (alternative format)
                                json.has("error") -> {
                                    val errorMsg = json.getString("error")
                                    Log.e(TAG, "streamSummaryV4: Error event: $errorMsg")
                                    emit(V4StreamEvent.Error(errorMsg))
                                    break
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "streamSummaryV4: Error parsing event: ${e.message}")
                            e.printStackTrace()
                            // Continue reading even if one event fails
                        }
                    }
                }

                Log.d(TAG, "streamSummaryV4: SSE connection closed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "streamSummaryV4: Connection failed: ${e.message}")
            Log.e(TAG, "streamSummaryV4: Exception type: ${e.javaClass.simpleName}")
            e.printStackTrace()
            throw e
        }
    }

    /**
     * Parses metadata event from JSON
     */
    private fun parseMetadataEvent(json: JSONObject): V4StreamEvent.Metadata {
        val data = json.getJSONObject("data")
        return V4StreamEvent.Metadata(
            inputType = data.getString("input_type"),
            url = data.optString("url").takeIf { it.isNotEmpty() },
            title = data.optString("title").takeIf { it.isNotEmpty() },
            author = data.optString("author").takeIf { it.isNotEmpty() },
            date = data.optString("date").takeIf { it.isNotEmpty() },
            siteName = data.optString("site_name").takeIf { it.isNotEmpty() },
            scrapeMethod = data.optString("scrape_method").takeIf { it.isNotEmpty() },
            scrapeLatencyMs = if (data.has("scrape_latency_ms")) data.getDouble("scrape_latency_ms") else null,
            extractedTextLength = if (data.has("extracted_text_length")) data.getInt("extracted_text_length") else null,
            textLength = if (data.has("text_length")) data.getInt("text_length") else null,
            style = data.getString("style")
        )
    }

    /**
     * Parses patch event from JSON and returns updated state
     */
    private fun parsePatchEvent(json: JSONObject): V4StreamEvent.Patch {
        // Parse delta (patch operation)
        val delta = if (json.isNull("delta")) {
            null
        } else {
            val deltaObj = json.getJSONObject("delta")
            val op = deltaObj.getString("op")
            when (op) {
                "set" -> PatchOperation.Set(
                    field = deltaObj.getString("field"),
                    value = deltaObj.get("value")
                )
                "append" -> PatchOperation.Append(
                    field = deltaObj.getString("field"),
                    value = deltaObj.getString("value")
                )
                "done" -> PatchOperation.Done
                else -> null
            }
        }

        // Parse state (complete structured summary)
        val stateObj = json.getJSONObject("state")
        val state = StructuredSummary(
            title = stateObj.optString("title").takeIf { !stateObj.isNull("title") },
            mainSummary = stateObj.optString("main_summary").takeIf { !stateObj.isNull("main_summary") },
            keyPoints = if (stateObj.has("key_points") && !stateObj.isNull("key_points")) {
                parseJsonArray(stateObj.getJSONArray("key_points"))
            } else {
                emptyList()
            },
            category = stateObj.optString("category").takeIf { !stateObj.isNull("category") },
            sentiment = stateObj.optString("sentiment").takeIf { !stateObj.isNull("sentiment") },
            readTimeMin = if (stateObj.has("read_time_min") && !stateObj.isNull("read_time_min")) {
                stateObj.getInt("read_time_min")
            } else {
                null
            }
        )

        return V4StreamEvent.Patch(
            delta = delta,
            state = state,
            done = json.getBoolean("done"),
            tokensUsed = json.getInt("tokens_used"),
            latencyMs = if (json.has("latency_ms")) json.getDouble("latency_ms") else null
        )
    }

    /**
     * Helper to parse JSON array to List<String>
     */
    private fun parseJsonArray(jsonArray: JSONArray): List<String> {
        return List(jsonArray.length()) { i ->
            jsonArray.getString(i)
        }
    }
}
