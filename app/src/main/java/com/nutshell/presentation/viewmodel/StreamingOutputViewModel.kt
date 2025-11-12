package com.nutshell.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutshell.data.model.StreamingResult
import com.nutshell.data.model.SummaryData
import com.nutshell.domain.repository.SummaryRepository
import com.nutshell.utils.ClipboardUtils
import com.nutshell.utils.ShareUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamingOutputViewModel @Inject constructor(
    private val repository: SummaryRepository,
    private val clipboardUtils: ClipboardUtils,
    private val shareUtils: ShareUtils,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val TAG = "StreamingOutputVM"
    }

    private val _uiState = MutableStateFlow(StreamingOutputUiState())
    val uiState: StateFlow<StreamingOutputUiState> = _uiState.asStateFlow()

    fun startStreaming(inputText: String) {
        if (inputText.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Input text is empty",
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isStreaming = true,
                streamingText = "",
                error = null,
            )

            // Collect from repository and display chunks as they arrive
            var chunkCount = 0
            repository.summarizeTextStreaming(inputText).collect { result ->
                when (result) {
                    is StreamingResult.Progress -> {
                        chunkCount++
                        // Append chunk and update UI state immediately
                        // Server already provides natural streaming pace (30-50ms between chunks)
                        val newText = _uiState.value.streamingText + result.text
                        _uiState.value = _uiState.value.copy(streamingText = newText)
                        Log.d(TAG, "Updated UI with chunk #$chunkCount, length: ${newText.length}")
                    }
                    is StreamingResult.Complete -> {
                        Log.d(TAG, "Streaming complete, total chunks: $chunkCount")
                        _uiState.value = _uiState.value.copy(
                            isStreaming = false,
                            summaryData = result.summaryData,
                            streamingText = "",
                        )
                    }
                    is StreamingResult.Error -> {
                        Log.e(TAG, "Streaming error: ${result.message}")
                        _uiState.value = _uiState.value.copy(
                            isStreaming = false,
                            error = result.message,
                        )
                    }
                }
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTabIndex = index)
    }

    fun getCurrentSummaryText(): String {
        val state = _uiState.value

        // If streaming, return streaming text
        if (state.isStreaming) {
            return state.streamingText
        }

        // If completed, return appropriate summary based on tab
        val summaryData = state.summaryData ?: return ""
        return when (state.selectedTabIndex) {
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
                summaryData = summaryData.copy(isSaved = !summaryData.isSaved),
            )
        }
    }

    fun resetState() {
        _uiState.value = StreamingOutputUiState()
    }
}

data class StreamingOutputUiState(
    val streamingText: String = "",
    val isStreaming: Boolean = false,
    val summaryData: SummaryData? = null,
    val selectedTabIndex: Int = 1, // Default to Medium
    val error: String? = null,
)
