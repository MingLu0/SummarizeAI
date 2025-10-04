package com.summarizeai.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.summarizeai.MainActivity
import com.summarizeai.ui.navigation.Screen
import com.summarizeai.ui.screens.home.HomeScreen
import com.summarizeai.ui.screens.output.OutputScreen
import com.summarizeai.ui.screens.history.HistoryScreen
import com.summarizeai.ui.screens.saved.SavedScreen
import com.summarizeai.ui.screens.settings.SettingsScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigationFlow_backButtonFromOutputScreen_navigatesToHome() {
        // Given - Start with Output screen
        composeTestRule.setContent {
            OutputScreen(
                onNavigateBack = { 
                    // Simulate navigation to Home
                    composeTestRule.setContent {
                        HomeScreen(
                            onNavigateToLoading = { },
                            onNavigateToOutput = { }
                        )
                    }
                },
                onNavigateToHome = { }
            )
        }

        // Verify we're on Output screen
        composeTestRule.onNodeWithText("Summary").assertIsDisplayed()

        // When - Click back button
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Then - Should be on Home screen
        composeTestRule.onNodeWithText("Summarize AI").assertIsDisplayed()
        composeTestRule.onNodeWithText("Paste or upload your text here...").assertIsDisplayed()
    }

    @Test
    fun navigationFlow_homeButtonFromOutputScreen_navigatesToHome() {
        // Given - Start with Output screen
        composeTestRule.setContent {
            OutputScreen(
                onNavigateBack = { },
                onNavigateToHome = { 
                    // Simulate navigation to Home
                    composeTestRule.setContent {
                        HomeScreen(
                            onNavigateToLoading = { },
                            onNavigateToOutput = { }
                        )
                    }
                }
            )
        }

        // Verify we're on Output screen
        composeTestRule.onNodeWithText("Summary").assertIsDisplayed()

        // When - Click Home button in action area
        composeTestRule.onNodeWithText("Home").performClick()

        // Then - Should be on Home screen
        composeTestRule.onNodeWithText("Summarize AI").assertIsDisplayed()
        composeTestRule.onNodeWithText("Paste or upload your text here...").assertIsDisplayed()
    }

    @Test
    fun navigationFlow_bottomTabNavigationFromOutputScreen_worksCorrectly() {
        // Given - Start with Output screen
        composeTestRule.setContent {
            OutputScreen(
                onNavigateBack = { },
                onNavigateToHome = { }
            )
        }

        // Verify we're on Output screen
        composeTestRule.onNodeWithText("Summary").assertIsDisplayed()

        // Test Home tab navigation
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithText("Summarize AI").assertIsDisplayed()

        // Navigate back to Output screen
        composeTestRule.setContent {
            OutputScreen(
                onNavigateBack = { },
                onNavigateToHome = { }
            )
        }

        // Test History tab navigation
        composeTestRule.onNodeWithText("History").performClick()
        composeTestRule.onNodeWithText("History").assertIsDisplayed()

        // Navigate back to Output screen
        composeTestRule.setContent {
            OutputScreen(
                onNavigateBack = { },
                onNavigateToHome = { }
            )
        }

        // Test Saved tab navigation
        composeTestRule.onNodeWithText("Saved").performClick()
        composeTestRule.onNodeWithText("Saved").assertIsDisplayed()

        // Navigate back to Output screen
        composeTestRule.setContent {
            OutputScreen(
                onNavigateBack = { },
                onNavigateToHome = { }
            )
        }

        // Test Settings tab navigation
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun navigationFlow_bottomNavigationVisibleOnOutputScreen() {
        // Given - Output screen is displayed
        composeTestRule.setContent {
            OutputScreen(
                onNavigateBack = { },
                onNavigateToHome = { }
            )
        }

        // Then - Bottom navigation should be visible
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("History").assertIsDisplayed()
        composeTestRule.onNodeWithText("Saved").assertIsDisplayed()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun navigationFlow_outputScreenElementsDisplayed() {
        // Given - Output screen is displayed
        composeTestRule.setContent {
            OutputScreen(
                onNavigateBack = { },
                onNavigateToHome = { }
            )
        }

        // Then - All Output screen elements should be displayed
        composeTestRule.onNodeWithText("Summary").assertIsDisplayed()
        composeTestRule.onNodeWithText("Short").assertIsDisplayed()
        composeTestRule.onNodeWithText("Medium").assertIsDisplayed()
        composeTestRule.onNodeWithText("Detailed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Copy").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onNodeWithText("Share").assertIsDisplayed()
        composeTestRule.onNodeWithText("Home").assertIsDisplayed() // Home button in action area
    }

    @Test
    fun navigationFlow_backButtonClickable() {
        // Given - Output screen is displayed
        composeTestRule.setContent {
            OutputScreen(
                onNavigateBack = { },
                onNavigateToHome = { }
            )
        }

        // Then - Back button should be clickable
        composeTestRule.onNodeWithContentDescription("Back")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun navigationFlow_homeButtonInActionAreaClickable() {
        // Given - Output screen is displayed
        composeTestRule.setContent {
            OutputScreen(
                onNavigateBack = { },
                onNavigateToHome = { }
            )
        }

        // Then - Home button in action area should be clickable
        composeTestRule.onNodeWithText("Home")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun navigationFlow_bottomTabsClickable() {
        // Given - Output screen is displayed
        composeTestRule.setContent {
            OutputScreen(
                onNavigateBack = { },
                onNavigateToHome = { }
            )
        }

        // Then - All bottom tabs should be clickable
        composeTestRule.onNodeWithText("Home")
            .assertIsDisplayed()
            .assertIsEnabled()
        
        composeTestRule.onNodeWithText("History")
            .assertIsDisplayed()
            .assertIsEnabled()
        
        composeTestRule.onNodeWithText("Saved")
            .assertIsDisplayed()
            .assertIsEnabled()
        
        composeTestRule.onNodeWithText("Settings")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
}
