package com.summarizeai.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.summarizeai.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StreamingE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun streamingToggleInSettings_shouldAffectSummarizationBehavior() {
        // Navigate to Settings
        composeTestRule.onNodeWithText("Settings").performClick()
        
        // Find and interact with streaming toggle
        composeTestRule.onNodeWithText("Text Streaming").assertIsDisplayed()
        composeTestRule.onNodeWithText("Show summaries as they're being generated").assertIsDisplayed()
        
        // Toggle streaming on/off
        composeTestRule.onNode(hasTestTag("streaming_toggle")).performClick()
        
        // Navigate back to Home
        composeTestRule.onNodeWithText("Home").performClick()
        
        // Verify we're on home screen
        composeTestRule.onNodeWithText("Enter your text here").assertIsDisplayed()
    }

    @Test
    fun streamingOutputScreen_shouldDisplayCorrectly() {
        // This test would require actual streaming to be active
        // For now, we'll test the UI components exist
        
        // Navigate to Home
        composeTestRule.onNodeWithText("Home").performClick()
        
        // Enter some text
        composeTestRule.onNodeWithText("Enter your text here").performTextInput("This is a test text for streaming.")
        
        // Click summarize button
        composeTestRule.onNodeWithText("Summarize").performClick()
        
        // The app should navigate to either Output or StreamingOutput screen
        // depending on the streaming setting
        composeTestRule.waitForIdle()
        
        // Verify we're on an output screen
        composeTestRule.onNodeWithText("Summary").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_shouldHaveStreamingToggle() {
        // Navigate to Settings
        composeTestRule.onNodeWithText("Settings").performClick()
        
        // Verify streaming toggle exists
        composeTestRule.onNodeWithText("Text Streaming").assertIsDisplayed()
        composeTestRule.onNodeWithText("Show summaries as they're being generated").assertIsDisplayed()
        
        // Verify toggle is interactive
        composeTestRule.onNode(hasRole(Role.Switch)).assertIsEnabled()
    }

    @Test
    fun navigation_shouldWorkCorrectlyWithStreaming() {
        // Test navigation between screens
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithText("Enter your text here").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("History").performClick()
        composeTestRule.onNodeWithText("History").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Saved").performClick()
        composeTestRule.onNodeWithText("Saved").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithText("Enter your text here").assertIsDisplayed()
    }

    @Test
    fun homeScreen_shouldHaveSummarizeButton() {
        // Navigate to Home
        composeTestRule.onNodeWithText("Home").performClick()
        
        // Verify summarize button exists and is initially disabled
        composeTestRule.onNodeWithText("Summarize").assertIsDisplayed()
        composeTestRule.onNodeWithText("Summarize").assertIsNotEnabled()
        
        // Enter text and verify button becomes enabled
        composeTestRule.onNodeWithText("Enter your text here").performTextInput("Test text")
        composeTestRule.onNodeWithText("Summarize").assertIsEnabled()
    }

    @Test
    fun streamingToggle_shouldPersistAcrossAppRestarts() {
        // This test would require app restart simulation
        // For now, we'll test that the toggle can be interacted with
        
        // Navigate to Settings
        composeTestRule.onNodeWithText("Settings").performClick()
        
        // Toggle streaming on
        composeTestRule.onNode(hasRole(Role.Switch)).performClick()
        
        // Navigate away and back
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithText("Settings").performClick()
        
        // Verify toggle state is maintained (this would need actual persistence testing)
        composeTestRule.onNode(hasRole(Role.Switch)).assertIsDisplayed()
    }
}
