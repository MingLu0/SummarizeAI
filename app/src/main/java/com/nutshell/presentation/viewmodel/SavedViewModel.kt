package com.nutshell.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutshell.data.model.SummaryData
import com.nutshell.domain.repository.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: SummaryRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(SavedUiState())
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    init {
        loadSavedItems()
        observeSearch()
    }

    private fun loadSavedItems() {
        viewModelScope.launch {
            repository.getSavedSummaries().collect { savedSummaries ->
                _uiState.value = _uiState.value.copy(
                    savedSummaries = savedSummaries,
                    isLoading = false,
                )
            }
        }
    }

    private fun observeSearch() {
        viewModelScope.launch {
            combine(
                repository.getSavedSummaries(),
                _searchQuery,
            ) { savedSummaries, query ->
                if (query.isBlank()) {
                    savedSummaries
                } else {
                    savedSummaries.filter { summary ->
                        summary.originalText.contains(query, ignoreCase = true) ||
                            summary.shortSummary.contains(query, ignoreCase = true) ||
                            summary.mediumSummary.contains(query, ignoreCase = true) ||
                            summary.detailedSummary.contains(query, ignoreCase = true)
                    }
                }
            }.collect { filteredSummaries ->
                _uiState.value = _uiState.value.copy(
                    filteredSummaries = filteredSummaries,
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun unsaveSummary(summary: SummaryData) {
        viewModelScope.launch {
            repository.toggleSaveStatus(summary.id)
        }
    }
}

data class SavedUiState(
    val savedSummaries: List<SummaryData> = emptyList(),
    val filteredSummaries: List<SummaryData> = emptyList(),
    val isLoading: Boolean = true,
)
