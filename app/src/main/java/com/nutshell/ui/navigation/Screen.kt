package com.nutshell.ui.navigation

import android.net.Uri

/**
 * Screen routes following best practices for nested navigation.
 * Top-level routes (Home, History, Saved, Settings) are bottom nav tabs.
 * Nested routes are prefixed with their parent tab for proper graph organization.
 */
sealed class Screen(val route: String) {
    // Top-level screens (no bottom bar)
    object Splash : Screen("splash")
    
    // Top-level routes (bottom nav tabs - parent graphs)
    object Home : Screen("home")
    object History : Screen("history")
    object Saved : Screen("saved")
    object Settings : Screen("settings")
    
    // Nested routes within Home graph
    object HomeScreen : Screen("home_screen")
    object Loading : Screen("home/loading")
    object Output : Screen("home/output")
    object StreamingOutput : Screen("home/streaming/{inputText}") {
        fun createRoute(inputText: String): String {
            val encodedText = Uri.encode(inputText)
            return "home/streaming/$encodedText"
        }
    }
    
    // Nested routes within History graph
    object HistoryScreen : Screen("history_screen")
    
    // Nested routes within Saved graph
    object SavedScreen : Screen("saved_screen")
    
    // Nested routes within Settings graph
    object SettingsScreen : Screen("settings_screen")
}
