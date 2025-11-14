package com.nutshell.ui.navigation

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.nutshell.data.local.preferences.SummaryLanguage
import com.nutshell.data.local.preferences.SummaryLength
import com.nutshell.data.local.preferences.ThemeMode
import com.nutshell.presentation.viewmodel.*
import com.nutshell.ui.screens.history.HistoryScreen
import com.nutshell.ui.screens.home.HomeScreen
import com.nutshell.ui.screens.loading.LoadingScreen
import com.nutshell.ui.screens.output.OutputScreen
import com.nutshell.ui.screens.output.StreamingOutputScreen
import com.nutshell.ui.screens.saved.SavedScreen
import com.nutshell.ui.screens.settings.SettingsScreen
import com.nutshell.ui.screens.splash.SplashScreen

// Animation specs for screen transitions
private val fadeInTransition = fadeIn(animationSpec = tween(300))
private val fadeOutTransition = fadeOut(animationSpec = tween(300))
private val slideInTransition = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(300),
)
private val slideOutTransition = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(300),
)
private val popEnterTransition = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(300),
) + fadeIn(animationSpec = tween(300))
private val popExitTransition = slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(300),
) + fadeOut(animationSpec = tween(300))

// Fast animations for tab switches
private val fastFadeIn = fadeIn(animationSpec = tween(150))
private val fastFadeOut = fadeOut(animationSpec = tween(150))

/**
 * Single unified NavHost following best practices.
 * Uses navigation() builder for nested graphs.
 */
@Composable
fun NutshellNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
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
    extractedContent: String?,
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    outputViewModel: OutputViewModel,
    streamingOutputViewModel: StreamingOutputViewModel,
    historyViewModel: HistoryViewModel,
    savedViewModel: SavedViewModel,
    webContentViewModel: WebContentViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier.padding(innerPadding),
    ) {
        // ====================
        // SPLASH SCREEN (No bottom bar)
        // ====================
        composable(
            route = Screen.Splash.route,
            enterTransition = { fadeInTransition },
            exitTransition = { fadeOutTransition },
        ) {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
            )
        }

        // ====================
        // HOME NESTED GRAPH
        // ====================
        navigation(
            startDestination = Screen.HomeScreen.route,
            route = Screen.Home.route,
        ) {
            composable(
                route = Screen.HomeScreen.route,
                enterTransition = { fastFadeIn },
                exitTransition = { fastFadeOut },
            ) {
                val webContentUiState by webContentViewModel.uiState.collectAsStateWithLifecycle()

                HomeScreen(
                    uiState = homeUiState,
                    webContentError = webContentUiState.error,
                    onUpdateTextInput = homeViewModel::updateTextInput,
                    onSummarizeText = homeViewModel::summarizeText,
                    onUploadFile = homeViewModel::uploadFile,
                    onNavigateToStreaming = { inputText ->
                        navController.navigate(Screen.StreamingOutput.createRoute(inputText))
                    },
                    onNavigateToOutput = {
                        navController.navigate(Screen.Output.route)
                    },
                    onClearNavigationFlags = homeViewModel::clearNavigationFlags,
                )
            }

            composable(
                route = Screen.Loading.route,
                enterTransition = { fadeInTransition + slideInTransition },
                exitTransition = { fadeOutTransition + slideOutTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition },
            ) {
                LoadingScreen(
                    onNavigateToOutput = {
                        navController.navigate(Screen.Output.route)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                )
            }

            composable(
                route = Screen.Output.route,
                enterTransition = { fadeInTransition + slideInTransition },
                exitTransition = { fadeOutTransition + slideOutTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition },
            ) {
                OutputScreen(
                    uiState = outputUiState,
                    onNavigateBack = {
                        homeViewModel.resetState()
                        navController.popBackStack()
                    },
                    onNavigateToHome = {
                        homeViewModel.resetState()
                        navController.navigate(Screen.HomeScreen.route) {
                            popUpTo(Screen.HomeScreen.route) {
                                inclusive = true
                            }
                        }
                    },
                    onSelectTab = outputViewModel::selectTab,
                    onCopyToClipboard = outputViewModel::copyToClipboard,
                    onShareSummary = outputViewModel::shareSummary,
                    onToggleSaveStatus = outputViewModel::toggleSaveStatus,
                )
            }

            composable(
                route = Screen.StreamingOutput.route,
                arguments = listOf(
                    navArgument("inputText") { type = NavType.StringType },
                ),
                enterTransition = { fadeInTransition + slideInTransition },
                exitTransition = { fadeOutTransition + slideOutTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition },
            ) { backStackEntry ->
                val inputText = backStackEntry.arguments?.getString("inputText")?.let {
                    Uri.decode(it)
                } ?: ""

                val streamingOutputUiState by streamingOutputViewModel.uiState.collectAsStateWithLifecycle()

                StreamingOutputScreen(
                    uiState = streamingOutputUiState,
                    inputText = inputText,
                    onNavigateBack = {
                        homeViewModel.resetState()
                        navController.popBackStack()
                    },
                    onNavigateToHome = {
                        homeViewModel.resetState()
                        navController.navigate(Screen.HomeScreen.route) {
                            popUpTo(Screen.HomeScreen.route) {
                                inclusive = false
                            }
                        }
                    },
                    onStartStreaming = streamingOutputViewModel::startStreaming,
                    onSelectTab = streamingOutputViewModel::selectTab,
                    onCopyToClipboard = streamingOutputViewModel::copyToClipboard,
                    onShareSummary = streamingOutputViewModel::shareSummary,
                    onToggleSaveStatus = streamingOutputViewModel::toggleSaveStatus,
                    onResetState = streamingOutputViewModel::resetState,
                )
            }
        }

        // ====================
        // HISTORY NESTED GRAPH
        // ====================
        navigation(
            startDestination = Screen.HistoryScreen.route,
            route = Screen.History.route,
        ) {
            composable(
                route = Screen.HistoryScreen.route,
                enterTransition = { fastFadeIn },
                exitTransition = { fastFadeOut },
            ) {
                HistoryScreen(
                    uiState = historyUiState,
                    searchQuery = historySearchQuery,
                    onUpdateSearchQuery = historyViewModel::updateSearchQuery,
                    onDeleteSummary = historyViewModel::deleteSummary,
                )
            }
        }

        // ====================
        // SAVED NESTED GRAPH
        // ====================
        navigation(
            startDestination = Screen.SavedScreen.route,
            route = Screen.Saved.route,
        ) {
            composable(
                route = Screen.SavedScreen.route,
                enterTransition = { fastFadeIn },
                exitTransition = { fastFadeOut },
            ) {
                SavedScreen(
                    uiState = savedUiState,
                    searchQuery = savedSearchQuery,
                    onUpdateSearchQuery = savedViewModel::updateSearchQuery,
                    onUnsaveSummary = savedViewModel::unsaveSummary,
                )
            }
        }

        // ====================
        // SETTINGS NESTED GRAPH
        // ====================
        navigation(
            startDestination = Screen.SettingsScreen.route,
            route = Screen.Settings.route,
        ) {
            composable(
                route = Screen.SettingsScreen.route,
                enterTransition = { fastFadeIn },
                exitTransition = { fastFadeOut },
            ) {
                SettingsScreen(
                    isStreamingEnabled = isStreamingEnabled,
                    themeMode = themeMode,
                    summaryLanguage = summaryLanguage,
                    summaryLength = summaryLength,
                    appVersion = appVersion,
                    onSetStreamingEnabled = settingsViewModel::setStreamingEnabled,
                    onSetThemeMode = settingsViewModel::setThemeMode,
                    onSetSummaryLanguage = settingsViewModel::setSummaryLanguage,
                    onSetSummaryLength = settingsViewModel::setSummaryLength,
                )
            }
        }
    }
}
