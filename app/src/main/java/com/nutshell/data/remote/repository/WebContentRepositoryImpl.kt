package com.nutshell.data.remote.repository

import com.nutshell.data.remote.extractor.WebContent
import com.nutshell.data.remote.extractor.WebContentExtractor
import com.nutshell.domain.repository.WebContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebContentRepositoryImpl @Inject constructor(
    private val webContentExtractor: WebContentExtractor,
) : WebContentRepository {

    override suspend fun extractWebContent(url: String): Result<WebContent> = withContext(Dispatchers.IO) {
        try {
            webContentExtractor.extractContent(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
