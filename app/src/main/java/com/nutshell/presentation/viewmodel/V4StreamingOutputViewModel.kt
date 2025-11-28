package com.nutshell.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutshell.data.model.StructuredSummary
import com.nutshell.data.model.V4StreamingResult
import com.nutshell.data.model.V4SummaryData
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
class V4StreamingOutputViewModel @Inject constructor(
    private val repository: SummaryRepository,
    private val clipboardUtils: ClipboardUtils,
    private val shareUtils: ShareUtils,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val TAG = "V4StreamingOutputVM"
    }

    private val _uiState = MutableStateFlow(V4StreamingOutputUiState())
    val uiState: StateFlow<V4StreamingOutputUiState> = _uiState.asStateFlow()

    /**
     * Starts V4 streaming with text or URL input
     */
    fun startStreaming(text: String? = null, url: String? = null, style: String = "executive") {
        if (text.isNullOrBlank() && url.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Input is empty",
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isStreaming = true,
                streamingText = "",
                error = null,
            )

            Log.d(TAG, "Starting V4 streaming with style: $style")

            repository.summarizeTextStreamingV4(
                text = text,
                url = url,
                style = style
            ).collect { result ->
                when (result) {
                    is V4StreamingResult.Metadata -> {
                        Log.d(TAG, "Received metadata: inputType=${result.inputType}")
                        _uiState.value = _uiState.value.copy(
                            metadata = result
                        )
                    }

                    is V4StreamingResult.Progress -> {
                        Log.d(TAG, "Progress update: tokens=${result.tokensUsed}")
                        // Build formatted text from structured summary
                        val formattedText = buildFormattedText(result.structuredSummary)
                        _uiState.value = _uiState.value.copy(
                            streamingText = formattedText,
                            tokensUsed = result.tokensUsed
                        )
                    }

                    is V4StreamingResult.Complete -> {
                        Log.d(TAG, "Streaming complete: tokens=${result.tokensUsed}")
                        val formattedText = buildFormattedText(result.summaryData)
                        _uiState.value = _uiState.value.copy(
                            isStreaming = false,
                            summaryData = result.summaryData,
                            streamingText = "",
                            displayText = formattedText,
                            tokensUsed = result.tokensUsed,
                            latencyMs = result.latencyMs
                        )
                    }

                    is V4StreamingResult.Error -> {
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

    /**
     * Builds formatted display text from structured summary
     */
    private fun buildFormattedText(summary: StructuredSummary): String {
        return buildString {
            summary.title?.let {
                append(it)
                append("\n\n")
            }

            summary.mainSummary?.let {
                append(it)
                append("\n\n")
            }

            if (summary.keyPoints.isNotEmpty()) {
                append("Key Points:\n")
                summary.keyPoints.forEach { point ->
                    append("• ")
                    append(point)
                    append("\n")
                }
                append("\n")
            }

            // Metadata line
            val metadata = buildList {
                summary.category?.let { add("Category: $it") }
                summary.sentiment?.let { add("Sentiment: $it") }
                summary.readTimeMin?.let { add("Read Time: $it min") }
            }
            if (metadata.isNotEmpty()) {
                append(metadata.joinToString(" | "))
            }
        }.trim()
    }

    /**
     * Builds formatted display text from V4SummaryData
     */
    private fun buildFormattedText(summaryData: V4SummaryData): String {
        return buildString {
            summaryData.title?.let {
                append(it)
                append("\n\n")
            }

            summaryData.mainSummary?.let {
                append(it)
                append("\n\n")
            }

            if (summaryData.keyPoints.isNotEmpty()) {
                append("Key Points:\n")
                summaryData.keyPoints.forEach { point ->
                    append("• ")
                    append(point)
                    append("\n")
                }
                append("\n")
            }

            // Metadata line
            val metadata = buildList {
                summaryData.category?.let { add("Category: $it") }
                summaryData.sentiment?.let { add("Sentiment: $it") }
                summaryData.readTimeMin?.let { add("Read Time: $it min") }
            }
            if (metadata.isNotEmpty()) {
                append(metadata.joinToString(" | "))
            }
        }.trim()
    }

    fun getCurrentSummaryText(): String {
        val state = _uiState.value

        // If streaming, return streaming text
        if (state.isStreaming) {
            return state.streamingText
        }

        // If completed, return display text
        return state.displayText
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
        _uiState.value = V4StreamingOutputUiState()
    }
}

data class V4StreamingOutputUiState(
    val streamingText: String = "",         // Formatted text during streaming
    val displayText: String = "",           // Final formatted text after completion
    val isStreaming: Boolean = false,
    val summaryData: V4SummaryData? = null,
    val metadata: V4StreamingResult.Metadata? = null,
    val tokensUsed: Int = 0,
    val latencyMs: Double? = null,
    val error: String? = null,
)
