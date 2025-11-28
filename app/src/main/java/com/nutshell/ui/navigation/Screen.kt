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

    object V4StreamingOutput : Screen("home/v4streaming?text={text}&url={url}&style={style}") {
        fun createRoute(text: String? = null, url: String? = null, style: String = "executive"): String {
            val encodedText = text?.let { Uri.encode(it) } ?: ""
            val encodedUrl = url?.let { Uri.encode(it) } ?: ""
            return "home/v4streaming?text=$encodedText&url=$encodedUrl&style=$style"
        }
    }

    // Nested routes within History graph
    object HistoryScreen : Screen("history_screen")
    
    // Nested routes within Saved graph
    object SavedScreen : Screen("saved_screen")
    
    // Nested routes within Settings graph
    object SettingsScreen : Screen("settings_screen")
}
