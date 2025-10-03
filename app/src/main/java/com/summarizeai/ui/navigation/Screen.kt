package com.summarizeai.ui.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Main : Screen("main")
    object Home : Screen("home")
    object History : Screen("history")
    object Saved : Screen("saved")
    object Settings : Screen("settings")
    object Loading : Screen("loading")
    object Output : Screen("output")
}
