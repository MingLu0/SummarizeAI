package com.nutshell.presentation.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutshell.data.remote.extractor.WebContentExtractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebContentViewModel @Inject constructor(
    private val webContentExtractor: WebContentExtractor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WebContentUiState())
    val uiState: StateFlow<WebContentUiState> = _uiState.asStateFlow()

    fun handleIntent(intent: Intent?) {
        try {
            when (intent?.action) {
                Intent.ACTION_SEND -> {
                    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    sharedText?.let { text ->
                        val url = extractUrlFromText(text)
                        if (url != null) {
//                            handleSharedUrl(url)
                            _uiState.value = _uiState.value.copy(
                                extractedContent = url,
                                isExtracting = false,
                                shouldNavigateToMain = true,
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = e.message ?: "Failed to handle intent",
            )
        }
    }

    fun handleSharedUrl(url: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                sharedUrl = url,
                isExtracting = true,
                error = null,
            )

            try {
                println("WebContentViewModel: Starting content extraction for URL: $url")
                webContentExtractor.extractContent(url)
                    .onSuccess { webContent ->
                        println("WebContentViewModel: Content extracted successfully, length: ${webContent.content.length}")
                        _uiState.value = _uiState.value.copy(
                            extractedContent = webContent.content,
                            isExtracting = false,
                            shouldNavigateToMain = true,
                        )
                    }
                    .onFailure { error ->
                        println("WebContentViewModel: Content extraction failed: ${error.message}")
                        _uiState.value = _uiState.value.copy(
                            extractedContent = "",
                            isExtracting = false,
                            shouldNavigateToMain = true,
                            error = error.message,
                        )
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    extractedContent = "",
                    isExtracting = false,
                    error = e.message,
                )
            }
        }
    }

    fun clearExtractedContent() {
        _uiState.value = _uiState.value.copy(
            extractedContent = null,
            sharedUrl = null,
            shouldNavigateToMain = false,
        )
    }

    fun clearNavigationFlag() {
        _uiState.value = _uiState.value.copy(
            shouldNavigateToMain = false,
        )
    }

    private fun extractUrlFromText(text: String): String? {
        val urlPattern = Regex("https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+")
        return urlPattern.find(text)?.value
    }
}

data class WebContentUiState(
    val sharedUrl: String? = null,
    val extractedContent: String? = null,
    val isExtracting: Boolean = false,
    val error: String? = null,
    val shouldNavigateToMain: Boolean = false,
)
