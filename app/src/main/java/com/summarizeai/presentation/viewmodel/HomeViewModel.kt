package com.summarizeai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.summarizeai.data.model.ApiResult
import com.summarizeai.data.model.SummaryData
import com.summarizeai.domain.repository.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SummaryRepository
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
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.summarizeText(currentText)
                .let { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                summaryData = result.data
                            )
                        }
                        is ApiResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                        is ApiResult.Loading -> {
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
}

data class HomeUiState(
    val textInput: String = "",
    val isLoading: Boolean = false,
    val isSummarizeEnabled: Boolean = false,
    val summaryData: SummaryData? = null,
    val error: String? = null
)
