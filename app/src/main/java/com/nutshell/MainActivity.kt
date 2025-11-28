package com.nutshell

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nutshell.data.local.preferences.ApiVersion
import com.nutshell.data.local.preferences.SummaryLanguage
import com.nutshell.data.local.preferences.SummaryLength
import com.nutshell.data.local.preferences.SummaryStyle
import com.nutshell.data.local.preferences.ThemeMode
import com.nutshell.presentation.viewmodel.*
import com.nutshell.ui.navigation.BottomNavigationBar
import com.nutshell.ui.navigation.NutshellNavHost
import com.nutshell.ui.navigation.Screen
import com.nutshell.ui.theme.NutshellTheme
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
            // Single NavController for entire app
            val navController = rememberNavController()

            // Track current destination
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = currentBackStackEntry?.destination
            val currentRoute = currentDestination?.route

            // OBSERVE ALL VIEWMODELS AT TOP LEVEL
            val homeViewModel: HomeViewModel = hiltViewModel()
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val outputViewModel: OutputViewModel = hiltViewModel()
            val streamingOutputViewModel: StreamingOutputViewModel = hiltViewModel()
            val v4StreamingOutputViewModel: V4StreamingOutputViewModel = hiltViewModel()
            val historyViewModel: HistoryViewModel = hiltViewModel()
            val savedViewModel: SavedViewModel = hiltViewModel()
            val webContentViewModel: WebContentViewModel = hiltViewModel()

            // COLLECT ALL STATE FLOWS
            val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
            val isStreamingEnabled by settingsViewModel.isStreamingEnabled.collectAsStateWithLifecycle(initialValue = true)
            val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            val summaryLanguage by settingsViewModel.summaryLanguage.collectAsStateWithLifecycle(initialValue = SummaryLanguage.ENGLISH)
            val summaryLength by settingsViewModel.summaryLength.collectAsStateWithLifecycle(initialValue = SummaryLength.SHORT)
            val apiVersion by settingsViewModel.apiVersion.collectAsStateWithLifecycle(initialValue = ApiVersion.V3)
            val summaryStyle by settingsViewModel.summaryStyle.collectAsStateWithLifecycle(initialValue = SummaryStyle.EXECUTIVE)
            val outputUiState by outputViewModel.uiState.collectAsStateWithLifecycle()
            val streamingOutputUiState by streamingOutputViewModel.uiState.collectAsStateWithLifecycle()
            val v4StreamingOutputUiState by v4StreamingOutputViewModel.uiState.collectAsStateWithLifecycle()
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

            // Unified intent navigation handler
            // Handles both extractedContent and shouldNavigateToMain in priority order
            LaunchedEffect(webContentUiState.extractedContent, webContentUiState.shouldNavigateToMain, apiVersion) {
                when {
                    // Priority 1: If we have extracted content, navigate directly to streaming
                    webContentUiState.extractedContent?.isNotBlank() == true -> {
                        val content = webContentUiState.extractedContent!!

                        // Check API version preference to determine which screen to navigate to
                        if (apiVersion == ApiVersion.V4) {
                            println("MainActivity: Intent with content detected, navigating to V4StreamingOutput")
                            navController.navigate(Screen.V4StreamingOutput.createRoute(text = content)) {
                                launchSingleTop = true
                            }
                        } else {
                            println("MainActivity: Intent with content detected, navigating to V3 StreamingOutput")
                            navController.navigate(Screen.StreamingOutput.createRoute(content)) {
                                launchSingleTop = true
                            }
                        }

                        // Clear after navigation to allow subsequent shares
                        kotlinx.coroutines.delay(500)
                        webContentViewModel.clearExtractedContent()
                        if (webContentUiState.shouldNavigateToMain) {
                            webContentViewModel.clearNavigationFlag()
                        }
                    }
                    // Priority 2: If shouldNavigateToMain but no content, navigate to Home
                    webContentUiState.shouldNavigateToMain -> {
                        println("MainActivity: Navigating to Home tab")
                        navController.navigate(Screen.Home.route) {
                            launchSingleTop = true
                        }
                        webContentViewModel.clearNavigationFlag()
                    }
                }
            }

            NutshellTheme(themeMode = themeMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (shouldShowTopAppBar(currentRoute)) {
                            TopAppBar(
                                currentRoute = currentRoute,
                                streamingOutputUiState = streamingOutputUiState,
                                v4StreamingOutputUiState = v4StreamingOutputUiState,
                                onNavigateBack = { navController.navigateUp() },
                                onCopyToClipboard = streamingOutputViewModel::copyToClipboard,
                                onToggleSaveStatus = streamingOutputViewModel::toggleSaveStatus,
                                onShareSummary = streamingOutputViewModel::shareSummary,
                                onV4CopyToClipboard = v4StreamingOutputViewModel::copyToClipboard,
                                onV4ToggleSaveStatus = v4StreamingOutputViewModel::toggleSaveStatus,
                                onV4ShareSummary = v4StreamingOutputViewModel::shareSummary,
                            )
                        }
                    },
                    bottomBar = {
                        if (shouldShowBottomBar(currentRoute)) {
                            BottomNavigationBar(
                                navController = navController,
                                currentDestination = currentDestination,
                            )
                        }
                    },
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
                        apiVersion = apiVersion,
                        summaryStyle = summaryStyle,
                        appVersion = BuildConfig.VERSION_NAME,
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
                        v4StreamingOutputViewModel = v4StreamingOutputViewModel,
                        historyViewModel = historyViewModel,
                        savedViewModel = savedViewModel,
                        webContentViewModel = webContentViewModel,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        println("MainActivity: onNewIntent called - action: ${intent?.action}")
        _intentFlow.value = intent
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    currentRoute: String?,
    streamingOutputUiState: StreamingOutputUiState,
    v4StreamingOutputUiState: V4StreamingOutputUiState,
    onNavigateBack: () -> Unit,
    onCopyToClipboard: () -> Unit,
    onToggleSaveStatus: () -> Unit,
    onShareSummary: () -> Unit,
    onV4CopyToClipboard: () -> Unit,
    onV4ToggleSaveStatus: () -> Unit,
    onV4ShareSummary: () -> Unit,
) {
    androidx.compose.material3.TopAppBar(
        title = {
            Text(
                text = getScreenTitle(currentRoute),
                modifier = Modifier.padding(start = 8.dp),
            )
        },
        navigationIcon = {
            if (shouldShowBackButton(currentRoute)) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            }
        },
        actions = {
            // Show action icons for V3 streaming output when streaming is complete
            if (currentRoute?.startsWith("home/streaming") == true &&
                !streamingOutputUiState.isStreaming &&
                streamingOutputUiState.summaryData != null
            ) {
                IconButton(onClick = onCopyToClipboard) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                    )
                }

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
                        },
                    )
                }

                IconButton(onClick = onShareSummary) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                    )
                }
            }
            // Show action icons for V4 streaming output when streaming is complete
            else if (currentRoute?.startsWith("home/v4streaming") == true &&
                !v4StreamingOutputUiState.isStreaming &&
                v4StreamingOutputUiState.summaryData != null
            ) {
                IconButton(onClick = onV4CopyToClipboard) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                    )
                }

                IconButton(onClick = onV4ToggleSaveStatus) {
                    Icon(
                        imageVector = if (v4StreamingOutputUiState.summaryData?.isSaved == true) {
                            Icons.Default.Bookmark
                        } else {
                            Icons.Default.BookmarkBorder
                        },
                        contentDescription = if (v4StreamingOutputUiState.summaryData?.isSaved == true) {
                            "Unsave"
                        } else {
                            "Save"
                        },
                    )
                }

                IconButton(onClick = onV4ShareSummary) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                    )
                }
            }
        },
    )
}

private fun shouldShowTopAppBar(route: String?): Boolean {
    return when (route) {
        null, "splash" -> false
        else -> true
    }
}

private fun shouldShowBottomBar(route: String?): Boolean {
    return when {
        route == null -> false
        route == "splash" -> false
        // Show for all tab routes and their nested screens
        route.startsWith("home") -> true
        route.startsWith("history") -> true
        route.startsWith("saved") -> true
        route.startsWith("settings") -> true
        else -> false
    }
}

private fun getScreenTitle(route: String?): String {
    return when {
        route == null -> ""
        route == "splash" -> ""
        route == "home_screen" -> "Home"
        route == "history_screen" -> "History"
        route == "saved_screen" -> "Saved"
        route == "settings_screen" -> "Settings"
        route == "home/output" -> "Summary"
        route.startsWith("home/streaming") -> "Summary"
        route.startsWith("home/v4streaming") -> "Summary"
        route == "home/loading" -> "Loading"
        else -> ""
    }
}

private fun shouldShowBackButton(route: String?): Boolean {
    return when {
        route == null -> false
        route == "home/output" -> true
        route.startsWith("home/streaming") -> true
        route.startsWith("home/v4streaming") -> true
        route == "home/loading" -> true
        else -> false
    }
}
