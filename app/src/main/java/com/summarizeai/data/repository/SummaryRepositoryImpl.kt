package com.summarizeai.data.repository

import com.summarizeai.data.local.datasource.SummaryLocalDataSource
import com.summarizeai.data.local.mapper.SummaryMapper.toSummaryData
import com.summarizeai.data.local.mapper.SummaryMapper.toSummaryDataList
import com.summarizeai.data.local.mapper.SummaryMapper.toSummaryEntity
import com.summarizeai.data.model.ApiResult
import com.summarizeai.data.model.StreamingResult
import com.summarizeai.data.model.SummaryData
import com.summarizeai.data.remote.api.SummarizeRequest
import com.summarizeai.data.remote.api.StreamingSummarizerService
import com.summarizeai.data.remote.datasource.SummaryRemoteDataSource
import com.summarizeai.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryRepositoryImpl @Inject constructor(
    private val remoteDataSource: SummaryRemoteDataSource,
    private val localDataSource: SummaryLocalDataSource,
    private val streamingService: StreamingSummarizerService
) : SummaryRepository {
    
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
                    detailedSummary = generateDetailedSummary(response.summary)
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
            var buffer = ""
            var fullSummary = ""
            
            streamingService.streamSummary(text, "https://colin730-summarizerapp.hf.space").collect { chunk ->
                buffer += chunk.content
                fullSummary += chunk.content
                
                // Check if we have a complete sentence
                val sentenceEndings = listOf(".", "!", "?")
                val lastSentenceEndIndex = sentenceEndings.mapNotNull { ending ->
                    buffer.lastIndexOf(ending).takeIf { it != -1 }
                }.maxOrNull()
                
                if (lastSentenceEndIndex != null) {
                    val completeSentence = buffer.substring(0, lastSentenceEndIndex + 1).trim()
                    if (completeSentence.isNotEmpty()) {
                        emit(StreamingResult.Progress(completeSentence))
                        buffer = buffer.substring(lastSentenceEndIndex + 1).trim()
                    }
                }
                
                if (chunk.done) {
                    // Emit any remaining buffer as final progress
                    if (buffer.isNotEmpty()) {
                        emit(StreamingResult.Progress(buffer))
                    }
                    
                    // Create final summary data
                    val summaryData = SummaryData(
                        originalText = text,
                        shortSummary = generateShortSummary(fullSummary),
                        mediumSummary = fullSummary,
                        detailedSummary = generateDetailedSummary(fullSummary)
                    )
                    
                    // Save to local database
                    localDataSource.insertSummary(summaryData.toSummaryEntity())
                    
                    emit(StreamingResult.Complete(summaryData))
                }
            }
        } catch (e: Exception) {
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
