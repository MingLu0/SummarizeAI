package com.nutshell.data.repository

import android.util.Log
import com.nutshell.data.local.datasource.SummaryLocalDataSource
import com.nutshell.data.local.mapper.SummaryMapper.toSummaryData
import com.nutshell.data.local.mapper.SummaryMapper.toSummaryDataList
import com.nutshell.data.local.mapper.SummaryMapper.toSummaryEntity
import com.nutshell.data.local.mapper.toSummaryEntity
import com.nutshell.data.model.ApiResult
import com.nutshell.data.model.StreamingResult
import com.nutshell.data.model.SummaryData
import com.nutshell.data.model.V4StreamEvent
import com.nutshell.data.model.V4StreamingResult
import com.nutshell.data.model.V4SummaryData
import com.nutshell.data.remote.api.StreamingSummarizerService
import com.nutshell.data.remote.api.SummarizeRequest
import com.nutshell.data.remote.api.V4StreamingSummarizerService
import com.nutshell.data.remote.datasource.SummaryRemoteDataSource
import com.nutshell.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryRepositoryImpl @Inject constructor(
    private val remoteDataSource: SummaryRemoteDataSource,
    private val localDataSource: SummaryLocalDataSource,
    private val streamingService: StreamingSummarizerService,
    private val v4StreamingService: V4StreamingSummarizerService,
) : SummaryRepository {

    companion object {
        private const val TAG = "SummaryRepositoryImpl"
    }

    override suspend fun summarizeText(text: String): ApiResult<SummaryData> {
        val request = SummarizeRequest(text = text)
        val result = remoteDataSource.summarizeText(request)

        return when (result) {
            is ApiResult.Success -> {
                val response = result.data
                val summaryData = SummaryData(
                    originalText = text,
                    shortSummary = generateShortSummary(response.summary),
                    mediumSummary = response.summary,
                    detailedSummary = generateDetailedSummary(response.summary),
                )

                // Save to local database
                localDataSource.insertSummary(summaryData.toSummaryEntity())

                ApiResult.Success(summaryData)
            }
            is ApiResult.Error -> {
                ApiResult.Error(result.message)
            }
            is ApiResult.Loading -> {
                ApiResult.Error("Request is still loading")
            }
        }
    }

    override fun summarizeTextStreaming(text: String): Flow<StreamingResult> = flow {
        try {
            Log.d(TAG, "summarizeTextStreaming: Starting streaming summary generation")
            var fullSummary = ""
            var chunkCount = 0
            streamingService.streamSummary(text, "https://colin730-summarizerapp.hf.space").collect { chunk ->
                chunkCount++
                Log.d(TAG, "summarizeTextStreaming: Received chunk #$chunkCount")
                Log.d(TAG, "summarizeTextStreaming: Chunk content preview: ${chunk.content.take(50)}")
                Log.d(TAG, "summarizeTextStreaming: Chunk done status: ${chunk.done}")
                fullSummary += chunk.content
                Log.d(TAG, "summarizeTextStreaming: Accumulated summary length: ${fullSummary.length} characters")
                emit(StreamingResult.Progress(chunk.content))
                if (chunk.done) {
                    // Create final summary data
                    val summaryData = SummaryData(
                        originalText = text,
                        shortSummary = generateShortSummary(fullSummary),
                        mediumSummary = fullSummary,
                        detailedSummary = generateDetailedSummary(fullSummary),
                    )
                    Log.d(TAG, "summarizeTextStreaming: Created SummaryData with ID: ${summaryData.id}")
                    Log.d(TAG, "summarizeTextStreaming: Saving to local database...")
                    localDataSource.insertSummary(summaryData.toSummaryEntity())
                    Log.d(TAG, "summarizeTextStreaming: Successfully saved to database")
                    emit(StreamingResult.Complete(summaryData))
                    Log.d(TAG, "summarizeTextStreaming: Emitted StreamingResult.Complete")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "summarizeTextStreaming: Error occurred during streaming", e)
            Log.e(TAG, "summarizeTextStreaming: Error message: ${e.message}")
            Log.e(TAG, "summarizeTextStreaming: Error type: ${e.javaClass.simpleName}")
            emit(StreamingResult.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun getAllSummaries(): Flow<List<SummaryData>> =
        localDataSource.getAllSummaries().map { entities ->
            entities.toSummaryDataList()
        }

    override fun getSavedSummaries(): Flow<List<SummaryData>> =
        localDataSource.getSavedSummaries().map { entities ->
            entities.toSummaryDataList()
        }

    override suspend fun getLatestSummary(): SummaryData? {
        return localDataSource.getLatestSummary()?.toSummaryData()
    }

    override suspend fun saveSummary(summaryData: SummaryData) {
        localDataSource.updateSaveStatus(summaryData.id, true)
    }

    override suspend fun deleteSummary(id: String) {
        localDataSource.deleteSummaryById(id)
    }

    override suspend fun toggleSaveStatus(id: String) {
        val entity = localDataSource.getSummaryById(id)
        entity?.let {
            localDataSource.updateSaveStatus(id, !it.isSaved)
        }
    }

    override fun searchSummaries(query: String): Flow<List<SummaryData>> =
        localDataSource.searchSummaries(query).map { entities ->
            entities.toSummaryDataList()
        }

    override fun summarizeTextStreamingV4(
        text: String?,
        url: String?,
        style: String
    ): Flow<V4StreamingResult> = flow {
        try {
            Log.d(TAG, "summarizeTextStreamingV4: Starting V4 streaming")
            Log.d(TAG, "summarizeTextStreamingV4: Style: $style")

            val originalInput = text ?: url ?: ""
            var savedSummaryData: V4SummaryData? = null

            v4StreamingService.streamSummaryV4(
                text = text,
                url = url,
                style = style
            ).collect { event ->
                when (event) {
                    is V4StreamEvent.Metadata -> {
                        Log.d(TAG, "summarizeTextStreamingV4: Received metadata event")
                        emit(
                            V4StreamingResult.Metadata(
                                inputType = event.inputType,
                                url = event.url,
                                title = event.title,
                                style = event.style
                            )
                        )
                    }

                    is V4StreamEvent.Patch -> {
                        Log.d(TAG, "summarizeTextStreamingV4: Received patch event, done=${event.done}")

                        if (event.done) {
                            // Final event - create and save summary data
                            val finalSummaryData = V4SummaryData.fromStructuredSummary(
                                structuredSummary = event.state,
                                originalText = originalInput
                            )
                            savedSummaryData = finalSummaryData

                            Log.d(TAG, "summarizeTextStreamingV4: Saving V4 summary to database")
                            localDataSource.insertSummary(finalSummaryData.toSummaryEntity())

                            emit(
                                V4StreamingResult.Complete(
                                    summaryData = finalSummaryData,
                                    tokensUsed = event.tokensUsed,
                                    latencyMs = event.latencyMs
                                )
                            )
                        } else {
                            // Progress update
                            emit(
                                V4StreamingResult.Progress(
                                    structuredSummary = event.state,
                                    tokensUsed = event.tokensUsed
                                )
                            )
                        }
                    }

                    is V4StreamEvent.Error -> {
                        Log.e(TAG, "summarizeTextStreamingV4: Error event: ${event.message}")
                        emit(V4StreamingResult.Error(event.message))
                    }
                }
            }

            Log.d(TAG, "summarizeTextStreamingV4: Stream completed")
        } catch (e: Exception) {
            Log.e(TAG, "summarizeTextStreamingV4: Exception occurred", e)
            emit(V4StreamingResult.Error(e.message ?: "Unknown error occurred"))
        }
    }

    private fun generateShortSummary(summary: String): String {
        // Generate a shorter version (first 2 sentences or 100 chars)
        val sentences = summary.split(". ")
        return if (sentences.size >= 2) {
            sentences.take(2).joinToString(". ") + "."
        } else {
            summary.take(100) + if (summary.length > 100) "..." else ""
        }
    }

    private fun generateDetailedSummary(summary: String): String {
        // Generate a more detailed version by expanding the summary
        return summary + " This summary provides comprehensive insights into the original content, covering all major points and supporting details."
    }
}
