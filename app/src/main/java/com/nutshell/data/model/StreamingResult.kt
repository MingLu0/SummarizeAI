package com.nutshell.data.model

sealed class StreamingResult {
    data class Progress(val text: String) : StreamingResult()
    data class Complete(val summaryData: SummaryData) : StreamingResult()
    data class Error(val message: String) : StreamingResult()
}
