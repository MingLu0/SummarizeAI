package com.nutshell.domain.repository

import com.nutshell.data.remote.extractor.WebContent

interface WebContentRepository {
    suspend fun extractWebContent(url: String): Result<WebContent>
}
