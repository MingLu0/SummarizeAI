package com.summarizeai.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.summarizeai.data.model.ApiResult
import com.summarizeai.data.model.SummaryData
import com.summarizeai.domain.repository.SummaryRepository
import com.summarizeai.utils.TextExtractionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SummaryRepository,
    private val textExtractionUtils: TextExtractionUtils
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun updateTextInput(text: String) {
        _uiState.value = _uiState.value.copy(
            textInput = text,
            isSummarizeEnabled = text.isNotBlank()
        )
    }
    
    fun summarizeText() {
        val currentText = _uiState.value.textInput
        if (currentText.isBlank()) return
        
        viewModelScope.launch {
            println("HomeViewModel: Starting API call")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.summarizeText(currentText)
                .let { result ->
                    println("HomeViewModel: API call completed with result: $result")
                    when (result) {
                        is ApiResult.Success -> {
                            println("HomeViewModel: API Success - updating state with summaryData")
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                summaryData = result.data
                            )
                            println("HomeViewModel: State updated - isLoading: false, summaryData: ${result.data}")
                        }
                        is ApiResult.Error -> {
                            println("HomeViewModel: API Error - ${result.message}")
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                        is ApiResult.Loading -> {
                            println("HomeViewModel: API still loading")
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }
                    }
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun resetState() {
        _uiState.value = HomeUiState()
    }
    
    fun uploadFile(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val result = textExtractionUtils.extractTextFromUri(uri)
                result.fold(
                    onSuccess = { extractedText ->
                        _uiState.value = _uiState.value.copy(
                            textInput = extractedText,
                            isLoading = false,
                            isSummarizeEnabled = extractedText.isNotBlank()
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to read file"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
}

data class HomeUiState(
    val textInput: String = "",
    val isLoading: Boolean = false,
    val isSummarizeEnabled: Boolean = false,
    val summaryData: SummaryData? = null,
    val error: String? = null
)
