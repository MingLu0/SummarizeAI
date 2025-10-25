package com.summarizeai.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.summarizeai.data.model.SummaryData
import com.summarizeai.presentation.viewmodel.OutputUiState
import com.summarizeai.ui.screens.output.OutputScreen
import com.summarizeai.ui.theme.SummarizeAITheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class OutputScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private fun createTestSummaryData() = SummaryData(
        id = "test-id",
        originalText = "Test original text",
        shortSummary = "Short test summary",
        mediumSummary = "Medium test summary",
        detailedSummary = "Detailed test summary",
        createdAt = Date(),
        isSaved = false
    )
    
    @Test
    fun outputScreen_displaysCorrectElements() {
        // When
        composeTestRule.setContent {
            SummarizeAITheme {
                OutputScreen(
                uiState = OutputUiState(summaryData = createTestSummaryData()),
                onNavigateBack = { },
                onNavigateToHome = { },
                onSelectTab = { },
                onCopyToClipboard = { },
                onShareSummary = { },
                onToggleSaveStatus = { }
                )
            }
        }
        
        composeTestRule.waitForIdle()
        
        // Then
        composeTestRule.onNodeWithText("Summary").assertIsDisplayed()
        composeTestRule.onNodeWithText("Short").assertIsDisplayed()
        composeTestRule.onNodeWithText("Medium").assertIsDisplayed()
        composeTestRule.onNodeWithText("Detailed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Copy").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onNodeWithText("Share").assertIsDisplayed()
    }
    
    @Test
    fun outputScreen_tabSelectionWorks() {
        // When
        composeTestRule.setContent {
            SummarizeAITheme {
                OutputScreen(
                    uiState = OutputUiState(summaryData = createTestSummaryData()),
                    onNavigateBack = { },
                    onNavigateToHome = { },
                    onSelectTab = { },
                    onCopyToClipboard = { },
                    onShareSummary = { },
                    onToggleSaveStatus = { }
                )
            }
        }
        
        composeTestRule.waitForIdle()
        
        // Click on Short tab
        composeTestRule.onNodeWithText("Short")
            .performClick()
        
        // Then
        composeTestRule.onNodeWithText("Short").assertIsDisplayed()
        
        // Click on Detailed tab
        composeTestRule.onNodeWithText("Detailed")
            .performClick()
        
        // Then
        composeTestRule.onNodeWithText("Detailed").assertIsDisplayed()
    }
    
    @Test
    fun outputScreen_actionButtonsClickable() {
        // When
        composeTestRule.setContent {
            SummarizeAITheme {
                OutputScreen(
                    uiState = OutputUiState(summaryData = createTestSummaryData()),
                    onNavigateBack = { },
                    onNavigateToHome = { },
                    onSelectTab = { },
                    onCopyToClipboard = { },
                    onShareSummary = { },
                    onToggleSaveStatus = { }
                )
            }
        }
        
        composeTestRule.waitForIdle()
        
        // Then - All action buttons should be clickable
        composeTestRule.onNodeWithText("Copy")
            .assertIsDisplayed()
            .assertIsEnabled()
        
        composeTestRule.onNodeWithText("Save")
            .assertIsDisplayed()
            .assertIsEnabled()
        
        composeTestRule.onNodeWithText("Share")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
}
