package com.summarizeai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.summarizeai.data.model.SummaryData
import com.summarizeai.domain.repository.SummaryRepository
import com.summarizeai.utils.ClipboardUtils
import com.summarizeai.utils.ShareUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OutputViewModel @Inject constructor(
    private val repository: SummaryRepository,
    private val clipboardUtils: ClipboardUtils,
    private val shareUtils: ShareUtils
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OutputUiState())
    val uiState: StateFlow<OutputUiState> = _uiState.asStateFlow()
    
    init {
        loadLatestSummary()
    }
    
    private fun loadLatestSummary() {
        viewModelScope.launch {
            val latestSummary = repository.getLatestSummary()
            if (latestSummary != null) {
                _uiState.value = _uiState.value.copy(summaryData = latestSummary)
            }
        }
    }
    
    fun setSummaryData(summaryData: SummaryData) {
        _uiState.value = _uiState.value.copy(summaryData = summaryData)
    }
    
    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTabIndex = index)
    }
    
    fun getCurrentSummaryText(): String {
        val summaryData = _uiState.value.summaryData ?: return ""
        return when (_uiState.value.selectedTabIndex) {
            0 -> summaryData.shortSummary
            1 -> summaryData.mediumSummary
            2 -> summaryData.detailedSummary
            else -> summaryData.mediumSummary
        }
    }
    
    fun copyToClipboard() {
        val text = getCurrentSummaryText()
        if (text.isNotBlank()) {
            clipboardUtils.copyToClipboard(text, "Summary")
        }
    }
    
    fun shareSummary() {
        val text = getCurrentSummaryText()
        if (text.isNotBlank()) {
            shareUtils.shareText(text, "Share Summary")
        }
    }
    
    fun toggleSaveStatus() {
        val summaryData = _uiState.value.summaryData ?: return
        
        viewModelScope.launch {
            repository.toggleSaveStatus(summaryData.id)
            
            // Update local state
            _uiState.value = _uiState.value.copy(
                summaryData = summaryData.copy(isSaved = !summaryData.isSaved)
            )
        }
    }
}

data class OutputUiState(
    val summaryData: SummaryData? = null,
    val selectedTabIndex: Int = 1, // Default to Medium
    val isSaved: Boolean = false
)
