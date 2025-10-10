package com.summarizeai.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.summarizeai.presentation.viewmodel.*

@Composable
fun AppScaffold(
    navController: NavHostController,
    homeUiState: HomeUiState,
    isStreamingEnabled: Boolean,
    outputUiState: OutputUiState,
    historyUiState: HistoryUiState,
    historySearchQuery: String,
    savedUiState: SavedUiState,
    savedSearchQuery: String,
    webContentUiState: WebContentUiState,
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    outputViewModel: OutputViewModel,
    streamingOutputViewModel: StreamingOutputViewModel,
    historyViewModel: HistoryViewModel,
    savedViewModel: SavedViewModel
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { _ ->
        SummarizeAINavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize(),
            homeUiState = homeUiState,
            isStreamingEnabled = isStreamingEnabled,
            outputUiState = outputUiState,
            historyUiState = historyUiState,
            historySearchQuery = historySearchQuery,
            savedUiState = savedUiState,
            savedSearchQuery = savedSearchQuery,
            extractedContent = webContentUiState.extractedContent,
            homeViewModel = homeViewModel,
            settingsViewModel = settingsViewModel,
            outputViewModel = outputViewModel,
            streamingOutputViewModel = streamingOutputViewModel,
            historyViewModel = historyViewModel,
            savedViewModel = savedViewModel
        )
    }
}

