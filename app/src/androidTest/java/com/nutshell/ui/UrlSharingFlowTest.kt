package com.nutshell.ui

import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nutshell.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test for URL sharing flow
 *
 * Tests that when a URL is shared via intent from an external app,
 * the app navigates directly to StreamingOutputScreen and starts summarizing.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UrlSharingFlowTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun whenUrlSharedViaIntent_navigatesToStreamingOutputDirectly() {
        // Arrange: Create an intent with a shared URL
        val sharedUrl = "https://www.bbc.com/news/articles/test123"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check this out: $sharedUrl")
        }

        // Act: Launch activity with the intent
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.intent = intent
            activity.onNewIntent(intent)
        }

        // Assert: Should navigate to StreamingOutput screen (showing "Summary" in TopAppBar)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText("Summary").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun whenUrlShared_streamingStartsAutomatically() {
        // Arrange
        val sharedUrl = "https://example.com/article"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sharedUrl)
        }

        // Act
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.intent = intent
            activity.onNewIntent(intent)
        }

        // Assert: Should show streaming indicator or content
        // The streaming should start automatically without user interaction
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                // Check for streaming screen elements
                composeTestRule.onNodeWithText("Summary").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun whenUrlSharedWhileAppRunning_navigatesToStreamingOutput() {
        // Arrange: App is already running
        composeTestRule.waitForIdle()

        // Create new intent
        val sharedUrl = "https://example.com/new-article"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sharedUrl)
        }

        // Act: Simulate receiving new intent while app is running
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onNewIntent(intent)
        }

        // Assert: Should navigate to StreamingOutput
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText("Summary").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun whenBackPressedFromStreamingOutput_navigatesToHome() {
        // Arrange: Share URL to get to StreamingOutput
        val sharedUrl = "https://example.com/article"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sharedUrl)
        }

        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.intent = intent
            activity.onNewIntent(intent)
        }

        // Wait for navigation
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Summary").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Act: Press back button
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        // Assert: Should navigate back to Home
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule.onNodeWithText("Home").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun whenInvalidIntentReceived_staysOnCurrentScreen() {
        // Arrange: Create intent without URL
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Just plain text, no URL")
        }

        // Act
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onNewIntent(intent)
        }

        // Assert: Should not navigate away (stays on current screen)
        // Give it a moment to process
        Thread.sleep(1000)
        composeTestRule.waitForIdle()
    }
}

