package com.nutshell.data.remote.extractor

// import android.util.Log - Removed for testing compatibility
import android.util.Log
import com.nutshell.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dankito.readability4j.Readability4J
import net.dankito.readability4j.extended.Readability4JExtended
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.SocketTimeoutException
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebContentExtractor @Inject constructor(
    private val networkUtils: NetworkUtils,
    private val okHttpClient: OkHttpClient
) {

    companion object {
        private const val TAG = "WebContentExtractor"
        private const val JSOUP_TIMEOUT_MS = 30000  // 30 seconds for Jsoup/Readability (reduced from 60s)
        private const val JINA_TIMEOUT_MS = 20000   // 20 seconds for Jina AI (faster API)
        private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
        private const val JINA_AI_BASE_URL = "https://r.jina.ai/"
    }

    suspend fun extractContent(url: String): Result<WebContent> = withContext(Dispatchers.IO) {
        try {
            Log.e(TAG, "═══ HYBRID EXTRACTION START ═══")
            Log.e(TAG, "URL: $url")
            
            // Check network connectivity first
            if (!networkUtils.isNetworkAvailable()) {
                Log.e(TAG, "❌ Network not available")
                return@withContext Result.failure(Exception("No network connection available"))
            }
            
            validateUrl(url)
            Log.e(TAG, "✓ URL validated successfully")
            
            // Strategy 1: Try Jina AI FIRST (fast, handles JavaScript, most reliable)
            Log.e(TAG, "→ Strategy 1: Trying Jina AI (20s timeout)...")
            tryJinaAI(url).onSuccess { content ->
                Log.e(TAG, "✅ SUCCESS with Jina AI - Length: ${content.content.length}")
                Log.e(TAG, "═══ EXTRACTION COMPLETE ═══")
                return@withContext Result.success(content)
            }.onFailure { error ->
                Log.e(TAG, "⚠ Jina AI failed: ${error.message}")
            }
            
            // Strategy 2: Try Readability4J (better parsing than basic Jsoup)
            Log.e(TAG, "→ Strategy 2: Trying Readability4J (30s timeout)...")
            tryReadability(url).onSuccess { content ->
                Log.e(TAG, "✅ SUCCESS with Readability4J - Length: ${content.content.length}")
                Log.e(TAG, "═══ EXTRACTION COMPLETE ═══")
                return@withContext Result.success(content)
            }.onFailure { error ->
                Log.e(TAG, "⚠ Readability4J failed: ${error.message}")
            }
            
            // Strategy 3: Try enhanced Jsoup with meta tags (final fallback)
            Log.e(TAG, "→ Strategy 3: Trying Enhanced Jsoup (30s timeout)...")
            tryJsoupExtraction(url).onSuccess { content ->
                Log.e(TAG, "✅ SUCCESS with Enhanced Jsoup - Length: ${content.content.length}")
                Log.e(TAG, "═══ EXTRACTION COMPLETE ═══")
                return@withContext Result.success(content)
            }.onFailure { error ->
                Log.e(TAG, "⚠ Enhanced Jsoup failed: ${error.message}")
            }
            
            // All strategies failed
            Log.e(TAG, "❌ ALL STRATEGIES FAILED")
            Log.e(TAG, "═══ EXTRACTION FAILED ═══")
            Result.failure(Exception("Unable to extract content. The website may require JavaScript or use anti-scraping measures. Please copy and paste the content manually."))
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Unexpected error: ${e.message}", e)
            Log.e(TAG, "═══ EXTRACTION FAILED ═══")
            Result.failure(Exception("Failed to extract content: ${e.message}"))
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
    
    // ═══════════════════════════════════════════════════════════════════════════
    // STRATEGY 1: Readability4J (Mozilla's article extraction algorithm)
    // ═══════════════════════════════════════════════════════════════════════════
    private fun tryReadability(url: String): Result<WebContent> {
        return try {
            // First fetch the HTML with Jsoup
            val document = fetchDocument(url)
            val html = document.html()
            
            // Then parse with Readability4J
            val readability = Readability4JExtended(url, html)
            val article = readability.parse()
            
            val title = article.title?.takeIf { it.isNotBlank() } ?: "Untitled"
            val content = article.textContent?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("No content extracted")
            
            if (content.length < 100) {
                throw IllegalStateException("Content too short: ${content.length} chars")
            }
            
            Log.e(TAG, "  ✓ Readability extracted - Title: $title, Content: ${content.length} chars")
            Result.success(WebContent(
                title = title,
                content = cleanText(content),
                url = url
            ))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Readability timeout: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Readability failed: ${e.message}"))
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // STRATEGY 2: Jina AI Reader (Handles JavaScript, free API)
    // ═══════════════════════════════════════════════════════════════════════════
    private fun tryJinaAI(url: String): Result<WebContent> {
        return try {
            val jinaUrl = "$JINA_AI_BASE_URL$url"
            Log.e(TAG, "  → Calling Jina AI: $jinaUrl")
            
            // Create a custom client with shorter timeout for Jina AI
            val jinaClient = okHttpClient.newBuilder()
                .connectTimeout(JINA_TIMEOUT_MS.toLong(), java.util.concurrent.TimeUnit.MILLISECONDS)
                .readTimeout(JINA_TIMEOUT_MS.toLong(), java.util.concurrent.TimeUnit.MILLISECONDS)
                .writeTimeout(JINA_TIMEOUT_MS.toLong(), java.util.concurrent.TimeUnit.MILLISECONDS)
                .build()
            
            val request = Request.Builder()
                .url(jinaUrl)
                .header("Accept", "text/plain")
                .header("X-Return-Format", "text")
                .build()
            
            val response = jinaClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                throw Exception("Jina AI returned ${response.code}")
            }
            
            val content = response.body?.string()?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("Empty response from Jina AI")
            
            if (content.length < 100) {
                throw IllegalStateException("Content too short: ${content.length} chars")
            }
            
            // Extract title from first line or use URL
            val lines = content.split("\n")
            val title = lines.firstOrNull { it.isNotBlank() && it.length < 200 } ?: "Untitled"
            
            Log.e(TAG, "  ✓ Jina AI extracted - Content: ${content.length} chars")
            Result.success(WebContent(
                title = title.take(200),
                content = cleanText(content),
                url = url
            ))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Jina AI timeout after ${JINA_TIMEOUT_MS/1000}s: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Jina AI failed: ${e.message}"))
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // STRATEGY 3: Enhanced Jsoup (Meta tags + Content selectors + Paragraphs)
    // ═══════════════════════════════════════════════════════════════════════════
    private fun tryJsoupExtraction(url: String): Result<WebContent> {
        return try {
            val document = fetchDocument(url)
            val content = parseContent(document)
            Result.success(content)
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Jsoup timeout: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Jsoup failed: ${e.message}"))
        }
    }

    private fun fetchDocument(url: String): Document {
        Log.e(TAG, "  → Fetching document from: $url")
        
        return Jsoup.connect(url)
            .userAgent(USER_AGENT)
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .header("Accept-Language", "en-US,en;q=0.9")
            .header("Accept-Encoding", "gzip, deflate, br")
            .header("Connection", "keep-alive")
            .header("Upgrade-Insecure-Requests", "1")
            .timeout(JSOUP_TIMEOUT_MS)  // 30 seconds timeout
            .followRedirects(true)
            .ignoreHttpErrors(false)
            .get()
    }

    private fun parseContent(document: Document): WebContent {
        // Try to get title from multiple sources
        val title = extractTitle(document)
        
        // Try to get content from multiple sources with priority order
        var contentText = extractFromMetaTags(document)
        
        if (contentText.isBlank()) {
            contentText = extractFromContentSelectors(document)
        }
        
        if (contentText.isBlank()) {
            contentText = extractFromParagraphs(document)
        }
        
        // Clean up the text
        val cleanedText = cleanText(contentText)
        
        if (cleanedText.isBlank() || cleanedText.length < 100) {
            Log.e(TAG, "Insufficient content extracted. Length: ${cleanedText.length}")
            throw IllegalStateException("No readable content found on the page. The website may require JavaScript or use dynamic loading.")
        }
        
        Log.e(TAG, "Successfully extracted content. Title: $title, Content length: ${cleanedText.length}")
        
        return WebContent(
            title = title,
            content = cleanedText,
            url = document.baseUri()
        )
    }
    
    private fun extractTitle(document: Document): String {
        // Try multiple title sources
        return document.select("meta[property='og:title']").attr("content").takeIf { it.isNotBlank() }
            ?: document.select("meta[name='twitter:title']").attr("content").takeIf { it.isNotBlank() }
            ?: document.select("h1").first()?.text()?.takeIf { it.isNotBlank() }
            ?: document.select("title").text().takeIf { it.isNotBlank() }
            ?: "Untitled"
    }
    
    private fun extractFromMetaTags(document: Document): String {
        // Try Open Graph description
        val ogDescription = document.select("meta[property='og:description']").attr("content")
        if (ogDescription.length > 100) {
            Log.e(TAG, "Extracted from og:description, length: ${ogDescription.length}")
            return ogDescription
        }
        
        // Try Twitter description
        val twitterDescription = document.select("meta[name='twitter:description']").attr("content")
        if (twitterDescription.length > 100) {
            Log.e(TAG, "Extracted from twitter:description, length: ${twitterDescription.length}")
            return twitterDescription
        }
        
        // Try standard meta description
        val metaDescription = document.select("meta[name='description']").attr("content")
        if (metaDescription.length > 100) {
            Log.e(TAG, "Extracted from meta description, length: ${metaDescription.length}")
            return metaDescription
        }
        
        return ""
    }
    
    private fun extractFromContentSelectors(document: Document): String {
        // Remove unwanted elements
        document.select("script, style, nav, footer, header, aside, .advertisement, .ad, .social-share").remove()
        
        // Enhanced list of content selectors with more variations
        val contentSelectors = listOf(
            "article[role='article']",
            "article.post",
            "article.article",
            "div.article-body",
            "div.story-body",
            "div.post-body",
            "div.entry-content",
            "div.article-content",
            "div[itemprop='articleBody']",
            "article",
            "main",
            "[role='main']",
            ".content",
            ".post-content",
            "#content",
            "#main",
            ".main-content"
        )
        
        var contentText = ""
        for (selector in contentSelectors) {
            val element = document.select(selector).first()
            if (element != null) {
                val text = element.text()
                if (text.length > contentText.length) {
                    contentText = text
                    Log.e(TAG, "Found content with selector '$selector', length: ${text.length}")
                }
            }
        }
        
        return contentText
    }
    
    private fun extractFromParagraphs(document: Document): String {
        // Remove unwanted elements
        document.select("script, style, nav, footer, header, aside").remove()
        
        // Get all paragraphs and combine them
        val paragraphs = document.select("p")
            .map { it.text() }
            .filter { it.length > 50 } // Filter out short paragraphs (likely navigation/ads)
            .take(20) // Take first 20 substantial paragraphs
        
        if (paragraphs.isNotEmpty()) {
            Log.e(TAG, "Extracted ${paragraphs.size} paragraphs")
            return paragraphs.joinToString("\n\n")
        }
        
        // Last resort: get all text from body
        Log.e(TAG, "Falling back to body text extraction")
        return document.select("body").text()
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
