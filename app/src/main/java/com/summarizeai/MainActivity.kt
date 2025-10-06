package com.summarizeai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.summarizeai.ui.navigation.Screen
import com.summarizeai.ui.navigation.SummarizeAINavHost
import com.summarizeai.ui.theme.SummarizeAITheme
import com.summarizeai.data.remote.extractor.WebContentExtractor
import com.summarizeai.data.remote.extractor.WebContent
import com.summarizeai.presentation.viewmodel.HomeViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var webContentExtractor: WebContentExtractor
    
    private var sharedUrl: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Handle intent data
        handleIntent(intent)
        
        setContent {
            SummarizeAITheme {
                val navController = rememberNavController()
                var extractedContent by remember { mutableStateOf<String?>(null) }
                
                // Process shared URL directly
                LaunchedEffect(sharedUrl) {
                    sharedUrl?.let { url ->
                        try {
                            // Extract web content first, then navigate
                            println("MainActivity: Starting content extraction for URL: $url")
                            webContentExtractor.extractContent(url)
                                .onSuccess { webContent ->
                                    println("MainActivity: Content extracted successfully, length: ${webContent.content.length}")
                                    // Store the extracted content and then navigate
                                    extractedContent = webContent.content
                                    // Navigate to Main screen after content is extracted
                                    navController.navigate(Screen.Main.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                }
                                .onFailure { error ->
                                    println("MainActivity: Content extraction failed: ${error.message}")
                                    // Navigate to Main screen even if extraction failed
                                    extractedContent = ""
                                    navController.navigate(Screen.Main.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                }
                        } catch (e: Exception) {
                            // Handle navigation errors gracefully
                            e.printStackTrace()
                        }
                    }
                }
                
                SummarizeAINavHost(
                    navController = navController,
                    modifier = Modifier.fillMaxSize(),
                    extractedContent = extractedContent
                )
            }
        }
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }
    
    private fun handleIntent(intent: Intent?) {
        try {
            when (intent?.action) {
                Intent.ACTION_SEND -> {
                    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    sharedText?.let { text ->
                        // Extract URL from shared text
                        val url = extractUrlFromText(text)
                        if (url != null) {
                            sharedUrl = url
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Handle any errors gracefully
            e.printStackTrace()
        }
    }
    
    private fun extractUrlFromText(text: String): String? {
        // Simple URL extraction - look for http/https URLs
        val urlPattern = Regex("https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+")
        return urlPattern.find(text)?.value
    }
}
