package com.summarizeai.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Main : Screen("main")
    object Home : Screen("home")
    object History : Screen("history")
    object Saved : Screen("saved")
    object Settings : Screen("settings")
    object Loading : Screen("loading")
    object Output : Screen("output")
    object StreamingOutput : Screen("streaming_output/{inputText}") {
        fun createRoute(inputText: String): String {
            val encodedText = Uri.encode(inputText)
            return "streaming_output/$encodedText"
        }
    }
    object WebPreview : Screen("webpreview")
}
