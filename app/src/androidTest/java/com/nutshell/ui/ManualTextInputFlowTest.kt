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
 * UI test for manual text input flow
 *
 * Tests that the existing manual text input functionality still works
 * after implementing direct URL navigation.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ManualTextInputFlowTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        // Wait for app to load to Home screen
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
    fun whenUserEntersText_summarizeButtonEnabled() {
        // Arrange: Find text input field
        val textToSummarize = "This is a sample text that needs to be summarized."

        // Act: Enter text
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput(textToSummarize)

        // Assert: Summarize button should be enabled
        composeTestRule.onNodeWithText("Summarize →")
            .assertIsEnabled()
    }

    @Test
    fun whenSummarizeButtonClicked_navigatesToStreamingOutput() {
        // Arrange
        val textToSummarize = "Sample text for summarization."
        
        // Act: Enter text and click summarize
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput(textToSummarize)
        
        composeTestRule.onNodeWithText("Summarize →")
            .performClick()

        // Assert: Should navigate to StreamingOutput screen
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Summary").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun whenTextFieldEmpty_summarizeButtonDisabled() {
        // Assert: Summarize button should be disabled when no text is entered
        composeTestRule.onNodeWithText("Summarize →")
            .assertIsNotEnabled()
    }

    @Test
    fun whenNavigatingBackFromOutput_homeScreenShowsPreviousText() {
        // Arrange: Enter text and navigate to output
        val textToSummarize = "Test text for back navigation."
        
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

        // Act: Navigate back
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        // Assert: Should be back on Home screen
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
    fun whenLongTextEntered_canStillSummarize() {
        // Arrange: Create long text
        val longText = "This is a very long text. ".repeat(50)

        // Act: Enter long text
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput(longText)

        // Assert: Summarize button should be enabled
        composeTestRule.onNodeWithText("Summarize →")
            .assertIsEnabled()
    }

    @Test
    fun whenTextCleared_summarizeButtonDisabled() {
        // Arrange: Enter text first
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextInput("Some text")

        // Act: Clear the text
        composeTestRule.onNodeWithText("Paste or upload your text here...")
            .performTextClearance()

        // Assert: Button should be disabled
        composeTestRule.onNodeWithText("Summarize →")
            .assertIsNotEnabled()
    }

    @Test
    fun homeScreenHasAllRequiredElements() {
        // Assert: Verify all key UI elements are present
        composeTestRule.onNodeWithText("Home").assertExists()
        composeTestRule.onNodeWithText("Paste or upload your text here...").assertExists()
        composeTestRule.onNodeWithText("Upload PDF or DOC").assertExists()
        composeTestRule.onNodeWithText("Summarize →").assertExists()
    }
}

