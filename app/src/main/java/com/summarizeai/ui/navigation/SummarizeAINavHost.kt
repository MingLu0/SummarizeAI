package com.summarizeai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.summarizeai.ui.screens.welcome.WelcomeScreen
import com.summarizeai.ui.screens.home.HomeScreen
import com.summarizeai.ui.screens.history.HistoryScreen
import com.summarizeai.ui.screens.saved.SavedScreen
import com.summarizeai.ui.screens.settings.SettingsScreen
import com.summarizeai.ui.screens.loading.LoadingScreen
import com.summarizeai.ui.screens.output.OutputScreen

@Composable
fun SummarizeAINavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
        modifier = modifier
    ) {
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
            MainScreenWithBottomNavigation(navController = navController)
        }
        
        composable(Screen.Loading.route) {
            LoadingScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Output.route) {
            OutputScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun MainScreenWithBottomNavigation(navController: NavHostController) {
    // TODO: Implement bottom navigation with 4 tabs
    // For now, show Home screen as placeholder
    HomeScreen(
        onNavigateToLoading = {
            navController.navigate(Screen.Loading.route)
        },
        onNavigateToOutput = {
            navController.navigate(Screen.Output.route)
        }
    )
}
