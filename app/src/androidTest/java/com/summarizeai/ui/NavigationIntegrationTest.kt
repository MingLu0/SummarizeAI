package com.summarizeai.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.summarizeai.MainActivity
import com.summarizeai.ui.navigation.SummarizeAINavHost
import com.summarizeai.ui.theme.SummarizeAITheme
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationIntegrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Composable
    private fun TestNavigationHost() {
        SummarizeAITheme {
            val navController = rememberNavController()
            SummarizeAINavHost(
                navController = navController,
                modifier = Modifier
            )
        }
    }

    @Test
    fun navigationIntegration_fullFlowFromHomeToOutputAndBack() {
        // Given - Start with the full navigation host
        composeTestRule.setContent {
            TestNavigationHost()
        }

        // Wait for splash screen to complete and navigate to main screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Summarize AI").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify we're on Home screen
        composeTestRule.onNodeWithText("Summarize AI").assertIsDisplayed()
        composeTestRule.onNodeWithText("Paste or upload your text here...").assertIsDisplayed()

        // Enter some text to enable the Summarize button
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput("This is a test text for summarization")

        // Click Summarize button
        composeTestRule.onNodeWithText("Summarize").performClick()

        // Wait for navigation to Output screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Summary").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify we're on Output screen
        composeTestRule.onNodeWithText("Summary").assertIsDisplayed()
        composeTestRule.onNodeWithText("Short").assertIsDisplayed()
        composeTestRule.onNodeWithText("Medium").assertIsDisplayed()
        composeTestRule.onNodeWithText("Detailed").assertIsDisplayed()

        // Verify bottom navigation is visible
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("History").assertIsDisplayed()
        composeTestRule.onNodeWithText("Saved").assertIsDisplayed()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()

        // Test back button navigation
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Wait for navigation back to Home
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Summarize AI").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify we're back on Home screen
        composeTestRule.onNodeWithText("Summarize AI").assertIsDisplayed()
    }

    @Test
    fun navigationIntegration_bottomTabNavigationFromOutputScreen() {
        // Given - Start with the full navigation host
        composeTestRule.setContent {
            TestNavigationHost()
        }

        // Wait for splash screen to complete
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Summarize AI").fetchSemanticsNodes().isNotEmpty()
        }

        // Navigate to Output screen (simulate the flow)
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput("Test text for summarization")
        composeTestRule.onNodeWithText("Summarize").performClick()

        // Wait for Output screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Summary").fetchSemanticsNodes().isNotEmpty()
        }

        // Test Home tab navigation
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Summarize AI").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Summarize AI").assertIsDisplayed()

        // Navigate back to Output screen
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput("Test text for summarization")
        composeTestRule.onNodeWithText("Summarize").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Summary").fetchSemanticsNodes().isNotEmpty()
        }

        // Test History tab navigation
        composeTestRule.onNodeWithText("History").performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("History").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("History").assertIsDisplayed()

        // Navigate back to Output screen
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Summarize AI").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput("Test text for summarization")
        composeTestRule.onNodeWithText("Summarize").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Summary").fetchSemanticsNodes().isNotEmpty()
        }

        // Test Saved tab navigation
        composeTestRule.onNodeWithText("Saved").performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Saved").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Saved").assertIsDisplayed()

        // Navigate back to Output screen
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Summarize AI").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput("Test text for summarization")
        composeTestRule.onNodeWithText("Summarize").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Summary").fetchSemanticsNodes().isNotEmpty()
        }

        // Test Settings tab navigation
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Settings").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun navigationIntegration_homeButtonInActionAreaNavigation() {
        // Given - Start with the full navigation host
        composeTestRule.setContent {
            TestNavigationHost()
        }

        // Wait for splash screen to complete
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Summarize AI").fetchSemanticsNodes().isNotEmpty()
        }

        // Navigate to Output screen
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput("Test text for summarization")
        composeTestRule.onNodeWithText("Summarize").performClick()

        // Wait for Output screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Summary").fetchSemanticsNodes().isNotEmpty()
        }

        // Test Home button in action area navigation
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Summarize AI").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify we're back on Home screen
        composeTestRule.onNodeWithText("Summarize AI").assertIsDisplayed()
    }
}
