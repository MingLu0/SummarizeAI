package com.nutshell.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nutshell.data.local.preferences.SummaryLanguage
import com.nutshell.data.local.preferences.SummaryLength
import com.nutshell.data.local.preferences.ThemeMode
import com.nutshell.presentation.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavHostController,
    currentRoute: String?,
    bottomNavRoute: String?,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onBottomNavRouteChange: (String?) -> Unit,
    homeUiState: HomeUiState,
    isStreamingEnabled: Boolean,
    themeMode: ThemeMode,
    summaryLanguage: SummaryLanguage,
    summaryLength: SummaryLength,
    appVersion: String,
    outputUiState: OutputUiState,
    streamingOutputUiState: StreamingOutputUiState,
    historyUiState: HistoryUiState,
    historySearchQuery: String,
    savedUiState: SavedUiState,
    savedSearchQuery: String,
    webContentUiState: WebContentUiState,
    onCopyToClipboard: () -> Unit,
    onShareSummary: () -> Unit,
    onToggleSaveStatus: () -> Unit,
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    outputViewModel: OutputViewModel,
    streamingOutputViewModel: StreamingOutputViewModel,
    historyViewModel: HistoryViewModel,
    savedViewModel: SavedViewModel,
    webContentViewModel: WebContentViewModel
) {
    // Use bottom nav route when main route is "main", otherwise use the main route
    val effectiveRoute = if (currentRoute == "main") bottomNavRoute else currentRoute
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (shouldShowTopAppBar(effectiveRoute)) {
                TopAppBar(
                    title = {
                        Text(
                            text = getScreenTitle(effectiveRoute),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    },
                    navigationIcon = {
                        if (shouldShowBackButton(effectiveRoute)) {
                            IconButton(
                                onClick = {
                                    // For streaming output, back button navigates to home
                                    if (effectiveRoute?.startsWith("streaming_output") == true) {
                                        onNavigateToHome()
                                    } else {
                                        onNavigateBack()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                    actions = {
                        // Show action icons only for streaming output screen when streaming is complete
                        if (effectiveRoute?.startsWith("streaming_output") == true &&
                            !streamingOutputUiState.isStreaming &&
                            streamingOutputUiState.summaryData != null
                        ) {
                            // Copy button
                            IconButton(onClick = onCopyToClipboard) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy"
                                )
                            }

                            // Save button (filled bookmark when saved, outline when not)
                            IconButton(onClick = onToggleSaveStatus) {
                                Icon(
                                    imageVector = if (streamingOutputUiState.summaryData?.isSaved == true) {
                                        Icons.Default.Bookmark
                                    } else {
                                        Icons.Default.BookmarkBorder
                                    },
                                    contentDescription = if (streamingOutputUiState.summaryData?.isSaved == true) {
                                        "Unsave"
                                    } else {
                                        "Save"
                                    }
                                )
                            }

                            // Share button
                            IconButton(onClick = onShareSummary) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share"
                                )
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NutshellNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize(),
            innerPadding = innerPadding,
            homeUiState = homeUiState,
            isStreamingEnabled = isStreamingEnabled,
            themeMode = themeMode,
            summaryLanguage = summaryLanguage,
            summaryLength = summaryLength,
            appVersion = appVersion,
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
            savedViewModel = savedViewModel,
            webContentViewModel = webContentViewModel,
            onBottomNavRouteChange = onBottomNavRouteChange
        )
    }
}

private fun shouldShowTopAppBar(route: String?): Boolean {
    // Hide TopAppBar only for splash screen
    // Show for all other screens including bottom nav screens
    return when (route) {
        null, "splash" -> false
        else -> true
    }
}

private fun getScreenTitle(route: String?): String {
    return when {
        route == null -> ""
        route == "splash" -> "NUTSHELL"
        route == "main" -> ""
        route == "home" -> "HOME"
        route == "history" -> "HISTORY"
        route == "saved" -> "SAVED"
        route == "settings" -> "SETTINGS"
        route == "output" -> "SUMMARY"
        route.startsWith("streaming_output") -> "SUMMARY"
        route == "webpreview" -> "WEB PREVIEW"
        route == "loading" -> "LOADING"
        else -> ""
    }
}

private fun shouldShowBackButton(route: String?): Boolean {
    return when {
        route == null -> false
        route == "output" -> true
        route.startsWith("streaming_output") -> true
        route == "webpreview" -> true
        route == "loading" -> true
        else -> false
    }
}

