# Implementation Plan: Direct Navigation to Streaming Output with Error Display

## Overview

When a URL is shared to the Nutshell app from an external source (e.g., browser), the app will now:
1. Extract the URL from the share intent
2. Navigate directly to StreamingOutputScreen (bypassing HomeScreen)
3. Automatically start streaming summarization using the V3 API endpoint
4. Display errors properly if extraction or summarization fails

## Background

### Current Flow (Broken)
```
Share Intent → WebContentViewModel.handleIntent()
    → extractedContent = URL (just the URL string, not content)
    → shouldNavigateToMain = true
    → Navigate to Main screen
    → HomeScreen receives URL in extractedContent
    → HomeScreen LaunchedEffect auto-navigates to StreamingOutputScreen
    → Streaming starts with URL string (not extracted content)
```

**Problems:**
- Line 30 in WebContentViewModel has `handleSharedUrl(url)` commented out
- URL string is passed instead of extracted article content
- Extra navigation step through HomeScreen is unnecessary
- No proper error display in StreamingOutputScreen

### New Flow (Fixed)
```
Share Intent → WebContentViewModel.handleIntent()
    → Extract URL from shared text
    → shouldNavigateToStreaming = true
    → MainActivity detects flag
    → Navigate to Main screen (top-level)
    → MainScreenWithBottomNavigation detects flag
    → Navigate to StreamingOutputScreen(url) using bottomNavController
    → StreamingOutputScreen auto-starts streaming
    → V3 API handles both extraction + summarization on backend
    → Display streaming result OR error message
```

**Key Insights:**
- The V3 API endpoint (`/api/v3/scrape-and-summarize/stream`) handles both web content extraction AND summarization on the backend, so client-side extraction is no longer needed.
- Navigation hierarchy: Top-level NavController (Splash → Main) → Nested NavController (Home, History, Output, StreamingOutput)
- Must respect navigation hierarchy: Can't navigate directly to nested routes from top-level NavController

## Implementation Details

### 1. WebContentViewModel.kt

**File:** `app/src/main/java/com/nutshell/presentation/viewmodel/WebContentViewModel.kt`

#### Change 1.1: Update WebContentUiState data class (Line 106)

**Current:**
```kotlin
data class WebContentUiState(
    val sharedUrl: String? = null,
    val extractedContent: String? = null,
    val isExtracting: Boolean = false,
    val error: String? = null,
    val shouldNavigateToMain: Boolean = false,
)
```

**New:**
```kotlin
data class WebContentUiState(
    val sharedUrl: String? = null,
    val extractedContent: String? = null,
    val isExtracting: Boolean = false,
    val error: String? = null,
    val shouldNavigateToMain: Boolean = false,
    val shouldNavigateToStreaming: Boolean = false, // NEW FIELD
)
```

#### Change 1.2: Modify handleIntent() method (Lines 22-45)

**Current:**
```kotlin
fun handleIntent(intent: Intent?) {
    try {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                sharedText?.let { text ->
                    val url = extractUrlFromText(text)
                    if (url != null) {
//                            handleSharedUrl(url)  // COMMENTED OUT
                        _uiState.value = _uiState.value.copy(
                            extractedContent = url,
                            isExtracting = false,
                            shouldNavigateToMain = true,
                        )
                    }
                }
            }
        }
    } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(
            error = e.message ?: "Failed to handle intent",
        )
    }
}
```

**New:**
```kotlin
fun handleIntent(intent: Intent?) {
    try {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                sharedText?.let { text ->
                    val url = extractUrlFromText(text)
                    if (url != null) {
                        // Navigate directly to streaming for shared URLs
                        _uiState.value = _uiState.value.copy(
                            sharedUrl = url,
                            shouldNavigateToStreaming = true,
                        )
                    }
                }
            }
        }
    } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(
            error = e.message ?: "Failed to handle intent",
        )
    }
}
```

#### Change 1.3: Add clearStreamingNavigationFlag() method

**Add after `clearNavigationFlag()` method (around line 98):**
```kotlin
fun clearStreamingNavigationFlag() {
    _uiState.value = _uiState.value.copy(
        shouldNavigateToStreaming = false,
    )
}
```

### 2. MainActivity.kt

**File:** `app/src/main/java/com/nutshell/MainActivity.kt`

#### Change 2.1: Add LaunchedEffect for streaming navigation

**Add after line 88 (after the existing web content navigation block):**

```kotlin
// Web content streaming navigation (for shared URLs)
// Navigate to Main screen first, then nested navigation handles StreamingOutput
LaunchedEffect(webContentUiState.shouldNavigateToStreaming) {
    if (webContentUiState.shouldNavigateToStreaming && webContentUiState.sharedUrl != null) {
        navController.navigate(Screen.Main.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
        // Don't clear flag yet - MainScreenWithBottomNavigation will handle it
    }
}
```

**Explanation:**
- Watches `shouldNavigateToStreaming` flag from WebContentViewModel
- When true and URL is available, navigates to Main screen (NOT directly to StreamingOutputScreen)
- This respects the navigation hierarchy (top-level → Main → nested routes)
- Clears splash screen from backstack
- Flag is cleared by MainScreenWithBottomNavigation after nested navigation completes

**Navigation Hierarchy Fix:**
The app has a two-level navigation structure:
1. **Top-level NavController**: Splash → Main
2. **Nested NavController** (inside Main): Home, History, Saved, Settings, Output, StreamingOutput

We cannot navigate directly from the top-level NavController to StreamingOutput because it lives in the nested NavController. Instead:
1. MainActivity navigates to Main screen
2. MainScreenWithBottomNavigation (inside Main) observes the flag and navigates to StreamingOutput

### 3. NutshellNavHost.kt / MainScreenWithBottomNavigation

**File:** `app/src/main/java/com/nutshell/ui/navigation/NutshellNavHost.kt`

#### Change 3.1: Add nested navigation handler

**Add after line 200 (after the existing bottom nav route reporting LaunchedEffect):**

```kotlin
// Handle shared URL navigation to StreamingOutput
val webContentUiState by webContentViewModel.uiState.collectAsStateWithLifecycle()
LaunchedEffect(webContentUiState.shouldNavigateToStreaming) {
    val sharedUrl = webContentUiState.sharedUrl
    if (webContentUiState.shouldNavigateToStreaming && sharedUrl != null) {
        bottomNavController.navigate(
            Screen.StreamingOutput.createRoute(sharedUrl)
        ) {
            // Start from Home as the base, don't clear backstack
            launchSingleTop = true
        }
        webContentViewModel.clearStreamingNavigationFlag()
    }
}
```

**Explanation:**
- Collects `webContentUiState` to observe the `shouldNavigateToStreaming` flag
- Uses `bottomNavController` (nested NavController) to navigate to StreamingOutput
- Passes the URL as a route parameter
- Uses `launchSingleTop` to avoid duplicate instances
- Clears the flag after navigation is triggered
- This completes the two-stage navigation: MainActivity → Main, Main → StreamingOutput

### 4. StreamingOutputScreen.kt

**File:** `app/src/main/java/com/nutshell/ui/screens/output/StreamingOutputScreen.kt`

#### Change 3.1: Add error display UI

**Add after the typing indicator AnimatedVisibility block (after line 121, before the streaming content):**

```kotlin
// Error Display - Flat with Border (matches design system)
AnimatedVisibility(
    visible = uiState.error != null,
    enter = fadeIn(
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    ) + slideInVertically(
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        initialOffsetY = { it / 4 }
    ),
    exit = fadeOut(
        animationSpec = tween(durationMillis = 300)
    ) + slideOutVertically(
        animationSpec = tween(durationMillis = 300),
        targetOffsetY = { -it / 4 }
    )
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(12.dp),
            )
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
        }

        Text(
            text = uiState.error ?: "An unknown error occurred",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = onNavigateToHome,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text("Go to home")
            }
        }
    }
}

Spacer(modifier = Modifier.height(16.dp))
```

**Required imports to add at top of file:**
```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
```

#### Change 3.2: Modify typing indicator visibility

**Update line 94 to hide typing indicator when error exists:**

```kotlin
AnimatedVisibility(
    visible = uiState.isStreaming && uiState.error == null, // Add error check
) {
    // ... existing typing indicator code
}
```

#### Change 3.3: Update content visibility

**Update line 144 to hide content when error exists:**

```kotlin
AnimatedVisibility(
    visible = (currentSummaryText.isNotEmpty() || uiState.isStreaming) && uiState.error == null,
    // ... rest of the code
)
```

### 5. HomeScreen.kt

**File:** `app/src/main/java/com/nutshell/ui/screens/home/HomeScreen.kt`

#### Change 5.1: Remove auto-navigation LaunchedEffect

**Find and comment out or remove the LaunchedEffect block (around lines 53-66):**

```kotlin
// REMOVED: Auto-navigation for shared URLs now handled by MainActivity
// LaunchedEffect(extractedContent) {
//     extractedContent?.let { content ->
//         if (content.isNotBlank()) {
//             println("HomeScreen: Extracted content received, length: ${content.length}")
//             println("HomeScreen: Content preview: ${content.take(100)}")
//
//             onUpdateTextInput(content)
//
//             onNavigateToStreaming(content)
//
//             delay(500)
//             onClearExtractedContent()
//         }
//     }
// }
```

**Explanation:**
- Shared URLs now navigate directly to StreamingOutputScreen via MainActivity
- HomeScreen no longer needs to handle this flow
- Manual text entry and file uploads continue to work normally

## Testing Plan

### Test Case 1: Share URL from Browser
1. Open browser (Chrome, Firefox, etc.)
2. Navigate to any article (e.g., Wikipedia, Medium, BBC News)
3. Tap Share → Select Nutshell app
4. **Expected:** App opens directly to StreamingOutputScreen with typing indicator
5. **Expected:** Summary streams in letter-by-letter
6. **Expected:** No HomeScreen visible during this flow

### Test Case 2: Error Handling - Invalid URL
1. Share a URL that returns 404 or is unreachable
2. **Expected:** Error card appears with clear message
3. **Expected:** Typing indicator is hidden
4. **Expected:** "Go to Home" button works

### Test Case 3: Error Handling - Paywall Detection
1. Share a paywalled article (e.g., NYTimes subscriber content)
2. **Expected:** Backend returns error about extraction failure
3. **Expected:** Error displayed in StreamingOutputScreen
4. **Expected:** User can navigate back to home

### Test Case 4: Manual Text Entry Still Works
1. Open app normally (not via share intent)
2. Tap Home tab
3. Enter text manually
4. Tap Summarize button
5. **Expected:** Navigation works based on Settings toggle (streaming vs traditional)
6. **Expected:** No interference from share flow changes

### Test Case 5: Settings Toggle Respected for Manual Entry
1. Go to Settings → Toggle "Enable Streaming" OFF
2. Enter text manually in HomeScreen
3. Tap Summarize
4. **Expected:** Uses traditional API (OutputScreen), not streaming
5. Share a URL from browser
6. **Expected:** Always uses streaming mode (settings ignored for shared URLs)

## Verification Checklist

- [ ] WebContentUiState has `shouldNavigateToStreaming` field
- [ ] `handleIntent()` sets the streaming flag correctly
- [ ] `clearStreamingNavigationFlag()` method added
- [ ] MainActivity has LaunchedEffect watching streaming flag
- [ ] Navigation to StreamingOutputScreen works with URL parameter
- [ ] Error UI displays in StreamingOutputScreen
- [ ] Error card has proper styling (border, colors, icons)
- [ ] Typing indicator hidden when error occurs
- [ ] "Go to Home" button works in error state
- [ ] HomeScreen auto-navigation removed/commented out
- [ ] Manual text entry still works
- [ ] File upload still works
- [ ] Backend errors propagate to UI correctly
- [ ] Logcat shows proper logging for debugging

## Architecture Notes

### Why Direct Navigation?

**Before:** Share → Main → Home → Streaming (3 screen transitions)
**After:** Share → Streaming (1 screen transition)

**Benefits:**
- Faster user experience (saves ~1 second)
- Clearer intent: URL sharing always means "summarize this"
- No confusing intermediate screens
- Consistent with user expectation (share = action)

### Why Always Stream for Shared URLs?

Shared URLs represent "active intent" - the user is explicitly asking for summarization NOW. Streaming provides immediate visual feedback that work is happening. The Settings toggle is more appropriate for passive/manual usage where users might prefer batch processing.

### Backend API Details

**Endpoint:** `POST /api/v3/scrape-and-summarize/stream`

**Request Body:**
```json
{
  "url": "https://example.com/article",
  "max_tokens": 256,
  "prompt": "Summarize the following text concisely:"
}
```

**Response:** Server-Sent Events (SSE) stream

```
data: {"content": "The", "done": false}
data: {"content": " article", "done": false}
data: {"content": " discusses...", "done": false}
data: {"content": "", "done": true}
```

**Error Response:**
```
data: {"error": "Failed to extract content: Paywall detected", "done": true}
```

The service handles extraction failures gracefully and returns errors as SSE events (see `StreamingSummarizerService.kt` lines 69-72).

## Future Enhancements

1. **Retry mechanism** - Allow users to retry failed extractions
2. **Progress indicator** - Show extraction progress before streaming starts
3. **URL preview** - Show domain/title before streaming (optional)
4. **Share multiple URLs** - Handle share intents with multiple URLs
5. **Deep link support** - Handle `nutshell://summarize?url=...` URLs
6. **Share from other apps** - Support sharing from Twitter, Reddit, etc.

## Rollback Plan

If issues occur, revert these commits and:
1. Restore `shouldNavigateToMain = true` in `handleIntent()`
2. Remove `shouldNavigateToStreaming` flag and navigation logic
3. Restore HomeScreen `LaunchedEffect(extractedContent)` block
4. App will return to previous behavior (Home → Streaming flow)

## Related Files

### Modified Files
- `app/src/main/java/com/nutshell/presentation/viewmodel/WebContentViewModel.kt` - Added `shouldNavigateToStreaming` flag and `clearStreamingNavigationFlag()` method
- `app/src/main/java/com/nutshell/MainActivity.kt` - Navigate to Main (not StreamingOutput directly)
- `app/src/main/java/com/nutshell/ui/navigation/NutshellNavHost.kt` - Added nested navigation handler in MainScreenWithBottomNavigation
- `app/src/main/java/com/nutshell/ui/screens/output/StreamingOutputScreen.kt` - Added error display UI
- `app/src/main/java/com/nutshell/ui/screens/home/HomeScreen.kt` - Removed auto-navigation LaunchedEffect

### Reference Files (No Changes)
- `app/src/main/java/com/nutshell/data/remote/api/StreamingSummarizerService.kt` - Backend API client
- `app/src/main/java/com/nutshell/presentation/viewmodel/StreamingOutputViewModel.kt` - Streaming state management
- `app/src/main/java/com/nutshell/ui/navigation/Screen.kt` - Route definitions

## Implementation Date

Created: 2025-11-14
Updated: 2025-11-14 (Fixed navigation hierarchy issue)

## Status

- [x] Plan documented
- [x] WebContentViewModel updated
- [x] MainActivity updated (fixed navigation hierarchy)
- [x] NutshellNavHost/MainScreenWithBottomNavigation updated (nested navigation handler)
- [x] StreamingOutputScreen updated
- [x] HomeScreen updated
- [x] Build successful
- [ ] Testing completed
- [ ] Code review completed
- [ ] Merged to main
