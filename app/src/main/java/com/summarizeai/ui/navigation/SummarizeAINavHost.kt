package com.summarizeai.ui.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.summarizeai.ui.screens.splash.SplashScreen
import com.summarizeai.ui.screens.welcome.WelcomeScreen
import com.summarizeai.ui.screens.home.HomeScreen
import com.summarizeai.ui.screens.history.HistoryScreen
import com.summarizeai.ui.screens.saved.SavedScreen
import com.summarizeai.ui.screens.settings.SettingsScreen
import com.summarizeai.ui.screens.loading.LoadingScreen
import com.summarizeai.ui.screens.output.OutputScreen
import com.summarizeai.ui.screens.output.StreamingOutputScreen
import com.summarizeai.presentation.viewmodel.*
import com.summarizeai.ui.theme.Cyan600
import com.summarizeai.ui.theme.Gray400
import com.summarizeai.ui.theme.Gray600
import com.summarizeai.ui.theme.SummarizeAITheme
import com.summarizeai.ui.theme.White

@Composable
fun SummarizeAINavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    homeUiState: com.summarizeai.presentation.viewmodel.HomeUiState,
    isStreamingEnabled: Boolean,
    outputUiState: com.summarizeai.presentation.viewmodel.OutputUiState,
    historyUiState: com.summarizeai.presentation.viewmodel.HistoryUiState,
    historySearchQuery: String,
    savedUiState: com.summarizeai.presentation.viewmodel.SavedUiState,
    savedSearchQuery: String,
    extractedContent: String?,
    homeViewModel: com.summarizeai.presentation.viewmodel.HomeViewModel,
    settingsViewModel: com.summarizeai.presentation.viewmodel.SettingsViewModel,
    outputViewModel: com.summarizeai.presentation.viewmodel.OutputViewModel,
    streamingOutputViewModel: com.summarizeai.presentation.viewmodel.StreamingOutputViewModel,
    historyViewModel: com.summarizeai.presentation.viewmodel.HistoryViewModel,
    savedViewModel: com.summarizeai.presentation.viewmodel.SavedViewModel,
    webContentViewModel: com.summarizeai.presentation.viewmodel.WebContentViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToWelcome = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStarted = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Main.route) {
            MainScreenWithBottomNavigation(
                navController = navController,
                homeUiState = homeUiState,
                isStreamingEnabled = isStreamingEnabled,
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
                webContentViewModel = webContentViewModel
            )
        }
        
    }
}

@Composable
fun MainScreenWithBottomNavigation(
    navController: NavHostController,
    homeUiState: HomeUiState,
    isStreamingEnabled: Boolean,
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
    webContentViewModel: WebContentViewModel
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
            NavigationBar(
                containerColor = White,
                contentColor = Gray600
            ) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (currentDestination?.hierarchy?.any { it.route == item.route } == true) Cyan600 else Gray400
                            )
                        },
                        label = { 
                            Text(
                                text = item.label,
                                color = if (currentDestination?.hierarchy?.any { it.route == item.route } == true) Cyan600 else Gray400
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
            composable(Screen.Home.route) {
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
            
            composable(Screen.History.route) {
                HistoryScreen(
                    uiState = historyUiState,
                    searchQuery = historySearchQuery,
                    onUpdateSearchQuery = historyViewModel::updateSearchQuery,
                    onDeleteSummary = historyViewModel::deleteSummary
                )
            }
            
            composable(Screen.Saved.route) {
                SavedScreen(
                    uiState = savedUiState,
                    searchQuery = savedSearchQuery,
                    onUpdateSearchQuery = savedViewModel::updateSearchQuery,
                    onUnsaveSummary = savedViewModel::unsaveSummary
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    isStreamingEnabled = isStreamingEnabled,
                    onSetStreamingEnabled = settingsViewModel::setStreamingEnabled
                )
            }
            
            composable(Screen.Loading.route) {
                LoadingScreen(
                    onNavigateToOutput = {
                        bottomNavController.navigate(Screen.Output.route)
                    },
                    onNavigateBack = {
                        bottomNavController.popBackStack()
                    }
                )
            }
            
            composable(Screen.Output.route) {
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
                )
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

