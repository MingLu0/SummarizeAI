package com.summarizeai.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.summarizeai.presentation.viewmodel.HomeUiState
import com.summarizeai.ui.screens.home.HomeScreen
import com.summarizeai.ui.theme.SummarizeAITheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun homeScreen_displaysCorrectElements() {
        // When
        composeTestRule.setContent {
            SummarizeAITheme {
                HomeScreen(
                    uiState = HomeUiState(),
                    extractedContent = null,
                    webContentError = null,
                    onUpdateTextInput = { },
                    onSummarizeText = { },
                    onClearError = { },
                    onUploadFile = { },
                    onNavigateToStreaming = { },
                    onNavigateToOutput = { },
                    onClearNavigationFlags = { },
                    onClearExtractedContent = { }
                )
            }
        }
        
        composeTestRule.waitForIdle()
        
        // Then
        composeTestRule.onNodeWithText("Summarize AI").assertIsDisplayed()
        composeTestRule.onNodeWithText("Paste or upload your text here...").assertIsDisplayed()
        composeTestRule.onNodeWithText("Upload").assertIsDisplayed()
        composeTestRule.onNodeWithText("Summarize").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_summarizeButtonDisabledWhenTextEmpty() {
        // When
        composeTestRule.setContent {
            SummarizeAITheme {
                HomeScreen(
                    uiState = HomeUiState(isSummarizeEnabled = false),
                    extractedContent = null,
                    webContentError = null,
                    onUpdateTextInput = { },
                    onSummarizeText = { },
                    onClearError = { },
                    onUploadFile = { },
                    onNavigateToStreaming = { },
                    onNavigateToOutput = { },
                    onClearNavigationFlags = { },
                    onClearExtractedContent = { }
                )
            }
        }
        
        composeTestRule.waitForIdle()
        
        // Then
        composeTestRule.onNodeWithText("Summarize")
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }
    
    @Test
    fun homeScreen_summarizeButtonEnabledWhenTextEntered() {
        // When
        composeTestRule.setContent {
            SummarizeAITheme {
                HomeScreen(
                    uiState = HomeUiState(
                        textInput = "Test text to summarize",
                        isSummarizeEnabled = true
                    ),
                    extractedContent = null,
                    webContentError = null,
                    onUpdateTextInput = { },
                    onSummarizeText = { },
                    onClearError = { },
                    onUploadFile = { },
                    onNavigateToStreaming = { },
                    onNavigateToOutput = { },
                    onClearNavigationFlags = { },
                    onClearExtractedContent = { }
                )
            }
        }
        
        composeTestRule.waitForIdle()
        
        // Then
        composeTestRule.onNodeWithText("Summarize")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    @Test
    fun homeScreen_uploadButtonClickable() {
        // When
        composeTestRule.setContent {
            SummarizeAITheme {
                HomeScreen(
                    uiState = HomeUiState(),
                    extractedContent = null,
                    webContentError = null,
                    onUpdateTextInput = { },
                    onSummarizeText = { },
                    onClearError = { },
                    onUploadFile = { },
                    onNavigateToStreaming = { },
                    onNavigateToOutput = { },
                    onClearNavigationFlags = { },
                    onClearExtractedContent = { }
                )
            }
        }
        
        composeTestRule.waitForIdle()
        
        // Then
        composeTestRule.onNodeWithText("Upload")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
}
