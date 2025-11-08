package com.nutshell.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutshell.data.remote.extractor.WebContent
import com.nutshell.domain.repository.WebContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebPreviewViewModel @Inject constructor(
    private val webContentRepository: WebContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WebPreviewUiState())
    val uiState: StateFlow<WebPreviewUiState> = _uiState.asStateFlow()

    fun setUrl(url: String) {
        _uiState.value = _uiState.value.copy(url = url)
    }

    fun extractContent() {
        val currentUrl = _uiState.value.url
        if (currentUrl.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                content = null
            )

            webContentRepository.extractWebContent(currentUrl)
                .onSuccess { content ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        content = content,
                        error = null
                    )
                }
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = getErrorMessage(throwable),
                        content = null
                    )
                }
        }
    }

    private fun getErrorMessage(throwable: Throwable): String {
        return when {
            throwable is IllegalArgumentException -> throwable.message ?: "Invalid URL"
            throwable.message?.contains("timeout", ignoreCase = true) == true -> 
                "Request timed out. Please check your internet connection and try again."
            throwable.message?.contains("network", ignoreCase = true) == true -> 
                "Network error. Please check your internet connection."
            throwable.message?.contains("no content", ignoreCase = true) == true -> 
                "No readable content found on this page."
            else -> "Failed to extract content. Please try again."
        }
    }
}

data class WebPreviewUiState(
    val url: String = "",
    val isLoading: Boolean = false,
    val content: WebContent? = null,
    val error: String? = null
)

