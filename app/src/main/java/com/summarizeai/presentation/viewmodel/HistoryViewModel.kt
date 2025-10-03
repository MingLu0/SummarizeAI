package com.summarizeai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.summarizeai.data.model.SummaryData
import com.summarizeai.domain.repository.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: SummaryRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    init {
        loadHistory()
        observeSearch()
    }
    
    private fun loadHistory() {
        viewModelScope.launch {
            repository.getAllSummaries().collect { summaries ->
                _uiState.value = _uiState.value.copy(
                    summaries = summaries,
                    isLoading = false
                )
            }
        }
    }
    
    private fun observeSearch() {
        viewModelScope.launch {
            combine(
                repository.getAllSummaries(),
                _searchQuery
            ) { summaries, query ->
                if (query.isBlank()) {
                    summaries
                } else {
                    summaries.filter { summary ->
                        summary.originalText.contains(query, ignoreCase = true) ||
                        summary.shortSummary.contains(query, ignoreCase = true) ||
                        summary.mediumSummary.contains(query, ignoreCase = true) ||
                        summary.detailedSummary.contains(query, ignoreCase = true)
                    }
                }
            }.collect { filteredSummaries ->
                _uiState.value = _uiState.value.copy(
                    filteredSummaries = filteredSummaries
                )
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun deleteSummary(summary: SummaryData) {
        viewModelScope.launch {
            repository.deleteSummary(summary.id)
        }
    }
}

data class HistoryUiState(
    val summaries: List<SummaryData> = emptyList(),
    val filteredSummaries: List<SummaryData> = emptyList(),
    val isLoading: Boolean = true
)
