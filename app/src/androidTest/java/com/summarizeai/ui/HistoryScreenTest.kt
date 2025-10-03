package com.summarizeai.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.summarizeai.ui.screens.history.HistoryScreen
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
            HistoryScreen()
        }
        
        // Then
        composeTestRule.onNodeWithText("History").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search history...").assertIsDisplayed()
    }
    
    @Test
    fun historyScreen_emptyStateDisplaysCorrectly() {
        // When
        composeTestRule.setContent {
            HistoryScreen()
        }
        
        // Then - Should show empty state
        composeTestRule.onNodeWithText("No History Yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your summarized texts will appear here").assertIsDisplayed()
    }
    
    @Test
    fun historyScreen_searchInputWorks() {
        // When
        composeTestRule.setContent {
            HistoryScreen()
        }
        
        // Enter search text
        composeTestRule.onNodeWithText("Search history...")
            .performTextInput("test search")
        
        // Then
        composeTestRule.onNodeWithText("test search").assertIsDisplayed()
    }
}
