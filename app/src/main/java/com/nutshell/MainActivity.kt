package com.nutshell

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.nutshell.BuildConfig
import com.nutshell.data.local.preferences.SummaryLanguage
import com.nutshell.data.local.preferences.SummaryLength
import com.nutshell.data.local.preferences.ThemeMode
import com.nutshell.ui.navigation.AppScaffold
import com.nutshell.ui.navigation.Screen
import com.nutshell.ui.theme.NutshellTheme
import com.nutshell.presentation.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val _intentFlow = MutableStateFlow<Intent?>(null)
    private val intentFlow = _intentFlow.asStateFlow()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Set initial intent
        _intentFlow.value = intent
        
        setContent {
                val navController = rememberNavController()
                
                // OBSERVE ALL VIEWMODELS AT TOP LEVEL
                val homeViewModel: HomeViewModel = hiltViewModel()
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                val outputViewModel: OutputViewModel = hiltViewModel()
                val streamingOutputViewModel: StreamingOutputViewModel = hiltViewModel()
                val historyViewModel: HistoryViewModel = hiltViewModel()
                val savedViewModel: SavedViewModel = hiltViewModel()
                val webContentViewModel: WebContentViewModel = hiltViewModel()
                
                // COLLECT ALL STATE FLOWS
                val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
                val isStreamingEnabled by settingsViewModel.isStreamingEnabled.collectAsStateWithLifecycle(initialValue = true)
                val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
                val summaryLanguage by settingsViewModel.summaryLanguage.collectAsStateWithLifecycle(initialValue = SummaryLanguage.ENGLISH)
                val summaryLength by settingsViewModel.summaryLength.collectAsStateWithLifecycle(initialValue = SummaryLength.SHORT)
                val outputUiState by outputViewModel.uiState.collectAsStateWithLifecycle()
                val historyUiState by historyViewModel.uiState.collectAsStateWithLifecycle()
                val historySearchQuery by historyViewModel.searchQuery.collectAsStateWithLifecycle()
                val savedUiState by savedViewModel.uiState.collectAsStateWithLifecycle()
                val savedSearchQuery by savedViewModel.searchQuery.collectAsStateWithLifecycle()
                val webContentUiState by webContentViewModel.uiState.collectAsStateWithLifecycle()
                val currentIntent by intentFlow.collectAsStateWithLifecycle()
                
                // Handle intent changes (both initial and new intents)
                LaunchedEffect(currentIntent) {
                    currentIntent?.let { intent ->
                        println("MainActivity: Handling intent - action: ${intent.action}")
                        webContentViewModel.handleIntent(intent)
                    }
                }
                
                // HANDLE ALL NAVIGATION LOGIC HERE
                // Web content navigation
                LaunchedEffect(webContentUiState.shouldNavigateToMain) {
                    if (webContentUiState.shouldNavigateToMain) {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                        webContentViewModel.clearNavigationFlag()
                    }
                }
                
                
                // PASS ALL STATE AND CALLBACKS DOWN
                NutshellTheme(themeMode = themeMode) {
                AppScaffold(
                    navController = navController,
                    homeUiState = homeUiState,
                    isStreamingEnabled = isStreamingEnabled,
                    themeMode = themeMode,
                    summaryLanguage = summaryLanguage,
                    summaryLength = summaryLength,
                    appVersion = BuildConfig.VERSION_NAME,
                    outputUiState = outputUiState,
                    historyUiState = historyUiState,
                    historySearchQuery = historySearchQuery,
                    savedUiState = savedUiState,
                    savedSearchQuery = savedSearchQuery,
                    webContentUiState = webContentUiState,
                    homeViewModel = homeViewModel,
                    settingsViewModel = settingsViewModel,
                    outputViewModel = outputViewModel,
                    streamingOutputViewModel = streamingOutputViewModel,
                    historyViewModel = historyViewModel,
                    savedViewModel = savedViewModel,
                    webContentViewModel = webContentViewModel
                )
                }
        }
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Emit new intent to flow so LaunchedEffect reacts to it
        println("MainActivity: onNewIntent called - action: ${intent?.action}")
        _intentFlow.value = intent
    }
}
