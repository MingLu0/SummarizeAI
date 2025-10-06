package com.summarizeai.domain.repository

import com.summarizeai.data.remote.extractor.WebContent

interface WebContentRepository {
    suspend fun extractWebContent(url: String): Result<WebContent>
}

