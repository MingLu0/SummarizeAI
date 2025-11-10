package com.nutshell.ui.navigation

import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nutshell.ui.screens.splash.SplashScreen
import com.nutshell.ui.screens.welcome.WelcomeScreen
import com.nutshell.ui.screens.home.HomeScreen
import com.nutshell.ui.screens.history.HistoryScreen
import com.nutshell.ui.screens.saved.SavedScreen
import com.nutshell.ui.screens.settings.SettingsScreen
import com.nutshell.ui.screens.loading.LoadingScreen
import com.nutshell.ui.screens.output.OutputScreen
import com.nutshell.ui.screens.output.StreamingOutputScreen
import com.nutshell.presentation.viewmodel.*
import com.nutshell.data.local.preferences.SummaryLanguage
import com.nutshell.data.local.preferences.SummaryLength
import com.nutshell.data.local.preferences.ThemeMode
import com.nutshell.ui.theme.Cyan600
import com.nutshell.ui.theme.Gray400
import com.nutshell.ui.theme.Gray600
import com.nutshell.ui.theme.NutshellTheme
import com.nutshell.ui.theme.White

// Animation specs for screen transitions
private val fadeInTransition = fadeIn(animationSpec = tween(300))
private val fadeOutTransition = fadeOut(animationSpec = tween(300))
private val slideInTransition = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(300)
)
private val slideOutTransition = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(300)
)
private val popEnterTransition = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(300)
) + fadeIn(animationSpec = tween(300))
private val popExitTransition = slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(300)
) + fadeOut(animationSpec = tween(300))

// Fast animations for bottom nav tab switches
private val fastFadeIn = fadeIn(animationSpec = tween(150))
private val fastFadeOut = fadeOut(animationSpec = tween(150))

@Composable
fun NutshellNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    homeUiState: com.nutshell.presentation.viewmodel.HomeUiState,
    isStreamingEnabled: Boolean,
    themeMode: ThemeMode,
    summaryLanguage: SummaryLanguage,
    summaryLength: SummaryLength,
    appVersion: String,
    outputUiState: com.nutshell.presentation.viewmodel.OutputUiState,
    historyUiState: com.nutshell.presentation.viewmodel.HistoryUiState,
    historySearchQuery: String,
    savedUiState: com.nutshell.presentation.viewmodel.SavedUiState,
    savedSearchQuery: String,
    extractedContent: String?,
    homeViewModel: com.nutshell.presentation.viewmodel.HomeViewModel,
    settingsViewModel: com.nutshell.presentation.viewmodel.SettingsViewModel,
    outputViewModel: com.nutshell.presentation.viewmodel.OutputViewModel,
    streamingOutputViewModel: com.nutshell.presentation.viewmodel.StreamingOutputViewModel,
    historyViewModel: com.nutshell.presentation.viewmodel.HistoryViewModel,
    savedViewModel: com.nutshell.presentation.viewmodel.SavedViewModel,
    webContentViewModel: com.nutshell.presentation.viewmodel.WebContentViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(
            route = Screen.Splash.route,
            enterTransition = { fadeInTransition },
            exitTransition = { fadeOutTransition }
        ) {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Welcome screen bypassed - app goes directly to Main screen from Splash
        // Uncomment to re-enable welcome screen for first-time users
        /*
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStarted = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
        */

        composable(
            route = Screen.Main.route,
            enterTransition = { fadeInTransition + slideInTransition },
            exitTransition = { fadeOutTransition + slideOutTransition }
        ) {
            MainScreenWithBottomNavigation(
                navController = navController,
                homeUiState = homeUiState,
                isStreamingEnabled = isStreamingEnabled,
                themeMode = themeMode,
                outputUiState = outputUiState,
                historyUiState = historyUiState,
                historySearchQuery = historySearchQuery,
                savedUiState = savedUiState,
                savedSearchQuery = savedSearchQuery,
                extractedContent = extractedContent,
                homeViewModel = homeViewModel,
                settingsViewModel = settingsViewModel,
                outputViewModel = outputViewModel,
                streamingOutputViewModel = streamingOutputViewModel,
                historyViewModel = historyViewModel,
                savedViewModel = savedViewModel,
                webContentViewModel = webContentViewModel,
                summaryLanguage = summaryLanguage,
                summaryLength = summaryLength,
                appVersion = appVersion,
            )
        }
        
    }
}

@Composable
fun MainScreenWithBottomNavigation(
    navController: NavHostController,
    homeUiState: HomeUiState,
    isStreamingEnabled: Boolean,
    themeMode: ThemeMode,
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
    summaryLanguage: SummaryLanguage,
    summaryLength: SummaryLength,
    appVersion: String,
) {
    val bottomNavController = rememberNavController()
    
    val bottomNavItems = listOf(
        BottomNavItem(
            route = Screen.Home.route,
            icon = Icons.Default.Home,
            label = "Home"
        ),
        BottomNavItem(
            route = Screen.History.route,
            icon = Icons.Default.History,
            label = "History"
        ),
        BottomNavItem(
            route = Screen.Saved.route,
            icon = Icons.Default.Bookmark,
            label = "Saved"
        ),
        BottomNavItem(
            route = Screen.Settings.route,
            icon = Icons.Default.Settings,
            label = "Settings"
        )
    )
    
    Scaffold(
        bottomBar = {
            val isDarkTheme = isSystemInDarkTheme()
            NavigationBar(
                containerColor = if (isDarkTheme) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.surfaceColorAtElevation(0.dp).copy(alpha = 0.95f)
                },
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 0.dp
            ) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (currentDestination?.hierarchy?.any { it.route == item.route } == true) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        label = { 
                            Text(
                                text = item.label,
                                color = if (currentDestination?.hierarchy?.any { it.route == item.route } == true) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            ) 
                        },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            println("Bottom nav clicked: ${item.route}, current: ${currentDestination?.route}")
                            
                            // Check if we're on Output or StreamingOutput screen
                            val isOnOutputScreen = currentDestination?.route == Screen.Output.route
                            val isOnStreamingOutputScreen = currentDestination?.route?.startsWith("streaming_output/") == true
                            
                            if (isOnOutputScreen || isOnStreamingOutputScreen) {
                                println("Navigating from Output/StreamingOutput screen to ${item.route}")
                                // If on Output or StreamingOutput screen, navigate directly to the selected tab
                                bottomNavController.navigate(item.route) {
                                    popUpTo(bottomNavController.graph.findStartDestination().id) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            } else {
                                println("Normal navigation to ${item.route}")
                                // Normal navigation for other screens
                                bottomNavController.navigate(item.route) {
                                    popUpTo(bottomNavController.graph.findStartDestination().id) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = Screen.Home.route,
                enterTransition = { fastFadeIn },
                exitTransition = { fastFadeOut }
            ) {
                // Collect webContentUiState to get error state
                val webContentUiState by webContentViewModel.uiState.collectAsStateWithLifecycle()

                HomeScreen(
                    uiState = homeUiState,
                    extractedContent = extractedContent,
                    webContentError = webContentUiState.error,
                    onUpdateTextInput = homeViewModel::updateTextInput,
                    onSummarizeText = homeViewModel::summarizeText,
                    onClearError = homeViewModel::clearError,
                    onUploadFile = homeViewModel::uploadFile,
                    onNavigateToStreaming = { inputText ->
                        bottomNavController.navigate(Screen.StreamingOutput.createRoute(inputText))
                    },
                    onNavigateToOutput = {
                        bottomNavController.navigate(Screen.Output.route)
                    },
                    onClearNavigationFlags = homeViewModel::clearNavigationFlags,
                    onClearExtractedContent = webContentViewModel::clearExtractedContent
                )
            }
            
            composable(
                route = Screen.History.route,
                enterTransition = { fastFadeIn },
                exitTransition = { fastFadeOut }
            ) {
                HistoryScreen(
                    uiState = historyUiState,
                    searchQuery = historySearchQuery,
                    onUpdateSearchQuery = historyViewModel::updateSearchQuery,
                    onDeleteSummary = historyViewModel::deleteSummary
                )
            }

            composable(
                route = Screen.Saved.route,
                enterTransition = { fastFadeIn },
                exitTransition = { fastFadeOut }
            ) {
                SavedScreen(
                    uiState = savedUiState,
                    searchQuery = savedSearchQuery,
                    onUpdateSearchQuery = savedViewModel::updateSearchQuery,
                    onUnsaveSummary = savedViewModel::unsaveSummary
                )
            }

            composable(
                route = Screen.Settings.route,
                enterTransition = { fastFadeIn },
                exitTransition = { fastFadeOut }
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
                    onSetSummaryLength = settingsViewModel::setSummaryLength
                )
            }
            
            composable(
                route = Screen.Loading.route,
                enterTransition = { fadeInTransition + slideInTransition },
                exitTransition = { fadeOutTransition + slideOutTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                LoadingScreen(
                    onNavigateToOutput = {
                        bottomNavController.navigate(Screen.Output.route)
                    },
                    onNavigateBack = {
                        bottomNavController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.Output.route,
                enterTransition = { fadeInTransition + slideInTransition },
                exitTransition = { fadeOutTransition + slideOutTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                OutputScreen(
                    uiState = outputUiState,
                    onNavigateBack = {
                        println("Back button clicked from Output screen")
                        homeViewModel.resetState()
                        bottomNavController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToHome = {
                        println("Home button clicked from Output screen")
                        homeViewModel.resetState()
                        bottomNavController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onSelectTab = outputViewModel::selectTab,
                    onCopyToClipboard = outputViewModel::copyToClipboard,
                    onShareSummary = outputViewModel::shareSummary,
                    onToggleSaveStatus = outputViewModel::toggleSaveStatus
                )
            }
            
            composable(
                route = Screen.StreamingOutput.route,
                arguments = listOf(
                    navArgument("inputText") { type = NavType.StringType }
                ),
                enterTransition = { fadeInTransition + slideInTransition },
                exitTransition = { fadeOutTransition + slideOutTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) { backStackEntry ->
                val inputText = backStackEntry.arguments?.getString("inputText")?.let { 
                    Uri.decode(it) 
                } ?: ""
                
                // Collect state INSIDE the composable lambda (maintains Single Source of Truth)
                val streamingOutputUiState by streamingOutputViewModel.uiState.collectAsStateWithLifecycle()
                
                StreamingOutputScreen(
                    uiState = streamingOutputUiState,
                    inputText = inputText,
                    onNavigateBack = {
                        println("Back button clicked from StreamingOutput screen")
                        homeViewModel.resetState()
                        bottomNavController.popBackStack()
                    },
                    onNavigateToHome = {
                        println("Home button clicked from StreamingOutput screen")
                        homeViewModel.resetState()
                        bottomNavController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    onStartStreaming = streamingOutputViewModel::startStreaming,
                    onSelectTab = streamingOutputViewModel::selectTab,
                    onCopyToClipboard = streamingOutputViewModel::copyToClipboard,
                    onShareSummary = streamingOutputViewModel::shareSummary,
                    onToggleSaveStatus = streamingOutputViewModel::toggleSaveStatus,
                    onResetState = streamingOutputViewModel::resetState
                )
            }
            
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

