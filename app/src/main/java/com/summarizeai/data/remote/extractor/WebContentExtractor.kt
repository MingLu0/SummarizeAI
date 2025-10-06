package com.summarizeai.data.remote.extractor

// import android.util.Log - Removed for testing compatibility
import com.summarizeai.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.SocketTimeoutException
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebContentExtractor @Inject constructor(
    private val networkUtils: NetworkUtils
) {

    companion object {
        private const val TAG = "WebContentExtractor"
        private const val TIMEOUT_MS = 30000  // Increased to 30 seconds for complex web pages
        private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
    }

    suspend fun extractContent(url: String): Result<WebContent> = withContext(Dispatchers.IO) {
        try {
            // Check network connectivity first
            if (!networkUtils.isNetworkAvailable()) {
                return@withContext Result.failure(Exception("No network connection available"))
            }
            
            validateUrl(url)
            val document = fetchDocument(url)
            val content = parseContent(document)
            Result.success(content)
        } catch (e: SocketTimeoutException) {
            // Log.e(TAG, "Timeout extracting content from $url", e) - Removed for testing compatibility
            Result.failure(Exception("Request timed out while extracting content from the webpage"))
        } catch (e: Exception) {
            // Log.e(TAG, "Error extracting content from $url", e) - Removed for testing compatibility
            Result.failure(e)
        }
    }

    private fun validateUrl(urlString: String) {
        try {
            val url = URL(urlString)
            if (url.protocol != "http" && url.protocol != "https") {
                throw IllegalArgumentException("Only HTTP and HTTPS URLs are supported")
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid URL format: $urlString")
        }
    }

    private fun fetchDocument(url: String): Document {
        return Jsoup.connect(url)
            .userAgent(USER_AGENT)
            .timeout(TIMEOUT_MS)
            .followRedirects(true)
            .get()
    }

    private fun parseContent(document: Document): WebContent {
        // Remove script and style elements
        document.select("script, style, nav, footer, header, aside").remove()
        
        // Try to get the main content
        val title = document.select("title").text().takeIf { it.isNotBlank() } ?: "Untitled"
        
        // Look for main content in common selectors
        val contentSelectors = listOf(
            "article",
            "main",
            "[role='main']",
            ".content",
            ".post-content",
            ".entry-content",
            ".article-content",
            "#content",
            "#main"
        )
        
        var contentText = ""
        for (selector in contentSelectors) {
            val element = document.select(selector).first()
            if (element != null && element.text().length > contentText.length) {
                contentText = element.text()
            }
        }
        
        // Fallback to body if no main content found
        if (contentText.isBlank()) {
            contentText = document.select("body").text()
        }
        
        // Clean up the text
        val cleanedText = cleanText(contentText)
        
        if (cleanedText.isBlank()) {
            throw IllegalStateException("No readable content found on the page")
        }
        
        return WebContent(
            title = title,
            content = cleanedText,
            url = document.baseUri()
        )
    }

    private fun cleanText(text: String): String {
        return text
            .replace(Regex("\\s+"), " ") // Replace multiple whitespace with single space
            .replace(Regex("\\n+"), "\n") // Replace multiple newlines with single newline
            .trim()
    }
}

data class WebContent(
    val title: String,
    val content: String,
    val url: String
)
