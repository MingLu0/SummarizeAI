package com.summarizeai.ui.navigation

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.summarizeai.ui.screens.splash.SplashScreen
import com.summarizeai.ui.screens.welcome.WelcomeScreen
import com.summarizeai.ui.screens.home.HomeScreen
import com.summarizeai.ui.screens.history.HistoryScreen
import com.summarizeai.ui.screens.saved.SavedScreen
import com.summarizeai.ui.screens.settings.SettingsScreen
import com.summarizeai.ui.screens.loading.LoadingScreen
import com.summarizeai.ui.screens.output.OutputScreen
import com.summarizeai.ui.theme.Cyan600
import com.summarizeai.ui.theme.Gray400
import com.summarizeai.ui.theme.Gray600
import com.summarizeai.ui.theme.SummarizeAITheme
import com.summarizeai.ui.theme.White

@Composable
fun SummarizeAINavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    extractedContent: String? = null
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
                extractedContent = extractedContent
            )
        }
    }
}

@Composable
fun MainScreenWithBottomNavigation(
    navController: NavHostController,
    extractedContent: String? = null
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
                            if (currentDestination?.route == Screen.Output.route) {
                                // If on Output screen, clear the entire stack and navigate to the selected tab
                                bottomNavController.navigate(item.route) {
                                    popUpTo(bottomNavController.graph.id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            } else {
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
                HomeScreen(
                    onNavigateToLoading = {
                        bottomNavController.navigate(Screen.Loading.route)
                    },
                    onNavigateToOutput = {
                        bottomNavController.navigate(Screen.Output.route)
                    },
                    extractedContent = extractedContent
                )
            }
            
            composable(Screen.History.route) {
                HistoryScreen()
            }
            
            composable(Screen.Saved.route) {
                SavedScreen()
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen()
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
                    onNavigateBack = {
                        println("Back button clicked from Output screen")
                        bottomNavController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToHome = {
                        println("Home button clicked from Output screen")
                        bottomNavController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
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

@Preview(showBackground = true)
@Composable
fun SummarizeAINavHostPreview() {
    SummarizeAITheme {
        SummarizeAINavHost(
            navController = rememberNavController(),
            extractedContent = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenWithBottomNavigationPreview() {
    SummarizeAITheme {
        MainScreenWithBottomNavigation(
            navController = rememberNavController(),
            extractedContent = null
        )
    }
}
