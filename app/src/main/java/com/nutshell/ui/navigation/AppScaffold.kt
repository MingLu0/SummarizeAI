package com.nutshell.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nutshell.data.local.preferences.SummaryLanguage
import com.nutshell.data.local.preferences.SummaryLength
import com.nutshell.data.local.preferences.ThemeMode
import com.nutshell.presentation.viewmodel.*
import com.nutshell.presentation.viewmodel.WebContentUiState

data class TopBarState(
    val title: String? = null,
    val showBackButton: Boolean = false,
    val onBackClick: (() -> Unit)? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavHostController,
    homeUiState: HomeUiState,
    isStreamingEnabled: Boolean,
    themeMode: ThemeMode,
    summaryLanguage: SummaryLanguage,
    summaryLength: SummaryLength,
    appVersion: String,
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
    savedViewModel: SavedViewModel,
    webContentViewModel: WebContentViewModel
) {
    var topBarState by remember { mutableStateOf(TopBarState()) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            topBarState.title?.let { title ->
                TopAppBar(
                    title = {
                        Text(
                            text = title,
                            fontSize = 24.sp
                        )
                    },
                    navigationIcon = {
                        if (topBarState.showBackButton) {
                            IconButton(onClick = { topBarState.onBackClick?.invoke() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    ) { innerPadding ->
        NutshellNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize(),
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
            contentPadding = innerPadding,
            onUpdateTopBar = { state -> topBarState = state }
        )
    }
}

