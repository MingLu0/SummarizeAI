package com.summarizeai.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.summarizeai.presentation.viewmodel.HistoryUiState
import com.summarizeai.ui.screens.history.HistoryScreen
import com.summarizeai.ui.theme.SummarizeAITheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistoryScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun historyScreen_displaysCorrectElements() {
        // When
        composeTestRule.setContent {
            SummarizeAITheme {
                HistoryScreen(
                uiState = HistoryUiState(isLoading = false),
                searchQuery = "",
                onUpdateSearchQuery = { },
                onDeleteSummary = { }
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("History").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search history...").assertIsDisplayed()
    }
    
    @Test
    fun historyScreen_emptyStateDisplaysCorrectly() {
        // When
        composeTestRule.setContent {
            SummarizeAITheme {
                HistoryScreen(
                    uiState = HistoryUiState(
                        summaries = emptyList(),
                        filteredSummaries = emptyList(),
                        isLoading = false
                    ),
                    searchQuery = "",
                    onUpdateSearchQuery = { },
                    onDeleteSummary = { }
                )
            }
        }
        
        // Then - Should show empty state
        composeTestRule.onNodeWithText("No History Yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your summarized texts will appear here").assertIsDisplayed()
    }
    
    @Test
    fun historyScreen_searchInputWorks() {
        // When
        composeTestRule.setContent {
            SummarizeAITheme {
                HistoryScreen(
                    uiState = HistoryUiState(isLoading = false),
                    searchQuery = "test search",
                    onUpdateSearchQuery = { },
                    onDeleteSummary = { }
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("test search").assertIsDisplayed()
    }
}
