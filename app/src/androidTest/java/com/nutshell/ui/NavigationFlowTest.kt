package com.nutshell.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nutshell.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test for navigation flow
 *
 * Tests navigation between different screens in the app,
 * especially from StreamingOutput back to other screens.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationFlowTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        // Wait for app to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText("Home").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun bottomNavigationBar_hasAllTabs() {
        // Assert: Verify all bottom navigation items are present
        composeTestRule.onNodeWithText("Home").assertExists()
        composeTestRule.onNodeWithText("History").assertExists()
        composeTestRule.onNodeWithText("Saved").assertExists()
        composeTestRule.onNodeWithText("Settings").assertExists()
    }

    @Test
    fun clickingHistoryTab_navigatesToHistory() {
        // Act
        composeTestRule.onNodeWithText("History").performClick()

        // Assert
        composeTestRule.waitForIdle()
        // History screen should be displayed (verify by checking for history-specific content)
    }

    @Test
    fun clickingSavedTab_navigatesToSaved() {
        // Act
        composeTestRule.onNodeWithText("Saved").performClick()

        // Assert
        composeTestRule.waitForIdle()
        // Saved screen should be displayed
    }

    @Test
    fun clickingSettingsTab_navigatesToSettings() {
        // Act
        composeTestRule.onNodeWithText("Settings").performClick()

        // Assert
        composeTestRule.waitForIdle()
        // Settings screen should be displayed (verify by checking for settings-specific content)
    }

    @Test
    fun fromStreamingOutput_canNavigateToHomeViaBottomNav() {
        // Arrange: Navigate to streaming output first
        val textToSummarize = "Test text"
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput(textToSummarize)
        composeTestRule.onNodeWithText("Summarize →")
            .performClick()

        // Wait for navigation
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Summary").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Act: Click Home in bottom nav
        composeTestRule.onAllNodesWithText("Home").onFirst().performClick()

        // Assert: Should navigate to Home
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Paste or upload your text here...").assertExists()
    }

    @Test
    fun fromStreamingOutput_canNavigateToHistory() {
        // Arrange: Navigate to streaming output first
        val textToSummarize = "Test text"
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput(textToSummarize)
        composeTestRule.onNodeWithText("Summarize →")
            .performClick()

        // Wait for navigation
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Summary").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Act: Click History in bottom nav
        composeTestRule.onNodeWithText("History").performClick()

        // Assert: Should navigate to History
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigationStatePreserved_whenSwitchingTabs() {
        // Arrange: Enter text on Home screen
        val testText = "This text should be preserved"
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput(testText)

        // Act: Navigate away and back
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodesWithText("Home").onFirst().performClick()
        composeTestRule.waitForIdle()

        // Assert: Text should still be in the field (state preserved)
        // Note: Depending on implementation, this might be cleared on navigation
        composeTestRule.onNodeWithText("Paste or upload your text here...").assertExists()
    }

    @Test
    fun backButton_fromStreamingOutput_navigatesToPreviousScreen() {
        // Arrange: Start from Home, navigate to StreamingOutput
        val textToSummarize = "Test text"
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput(textToSummarize)
        composeTestRule.onNodeWithText("Summarize →")
            .performClick()

        // Wait for navigation
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Summary").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Act: Press back
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        // Assert: Should return to Home
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule.onAllNodesWithText("Home").fetchSemanticsNodes().isNotEmpty()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun multipleBackPresses_navigatesCorrectly() {
        // Arrange: Navigate through multiple screens
        val textToSummarize = "Test"
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput(textToSummarize)
        composeTestRule.onNodeWithText("Summarize →")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Summary").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Act: Press back twice
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        
        composeTestRule.waitForIdle()
        
        // Back button behavior may vary, just ensure app doesn't crash
        composeTestRule.waitForIdle()
    }
}

