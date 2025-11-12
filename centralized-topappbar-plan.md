# Centralized Top App Bar Implementation Plan

**Date:** 2025-11-11
**Feature:** Centralized TopAppBar in AppScaffold with dynamic titles

## Overview

Implement a centralized Material3 TopAppBar in AppScaffold that displays dynamic titles based on the current navigation route, replacing duplicate title implementations across all screens.

## User Requirements

Based on clarification questions:
- ✅ Show back button for non-bottom-nav screens (Output, StreamingOutput, WebPreview, Loading)
- ✅ Use standard Material3 TopAppBar style (22.sp)
- ✅ TopAppBar with start-aligned title
- ✅ StreamingOutput screen should show "SUMMARY" title (same as Output)

## Current State Analysis

### AppScaffold.kt
- Simple outer wrapper with basic Material3 Scaffold
- Currently NO top app bar implementation
- Receives all ViewModels and UI states from MainActivity
- Does not consume scaffold's innerPadding

### Current Title Pattern in Screens
All 4 bottom nav screens use identical styling:
- **32.sp fontSize**
- **Bold fontWeight**
- **displayLarge typography**
- **Uppercase text**
- Status bar padding on root Column

Screens with titles:
1. **HomeScreen** (line ~107-117): "SUMMARIZE AI"
2. **HistoryScreen** (line ~58-67): "HISTORY"
3. **SavedScreen** (line ~58-66): "SAVED"
4. **SettingsScreen** (line ~59-68): "SETTINGS"
5. **OutputScreen** (line ~68-94): Custom header Row with back button + "SUMMARY"

## Implementation Plan

### 1. Update AppScaffold.kt

**Add parameters:**
```kotlin
currentRoute: String?,
onNavigateBack: () -> Unit
```

**Implement TopAppBar:**
- Use Material3 `TopAppBar` component (start-aligned)
- Dynamic title based on route mapping
- Conditional back button for non-bottom-nav screens
- Standard Material3 styling (~22.sp, per TopAppBar defaults)

**Route-to-Title Mapping:**
```
home → "HOME"
history → "HISTORY"
saved → "SAVED"
settings → "SETTINGS"
output → "SUMMARY"
streaming_output/{inputText} → "SUMMARY"
webpreview → "WEB PREVIEW"
loading → "LOADING"
splash → "NUTSHELL" (or empty)
main → "" (empty, as this is just a container)
```

**Back Button Visibility Logic:**
Show back button for: `output`, `streaming_output`, `webpreview`, `loading`
Hide back button for: `home`, `history`, `saved`, `settings`, `splash`, `main`

**Helper Functions:**
```kotlin
private fun getScreenTitle(route: String?): String
private fun shouldShowBackButton(route: String?): Boolean
```

### 2. Update MainActivity.kt

**Track current route:**
```kotlin
val currentBackStackEntry by navController.currentBackStackEntryAsState()
val currentRoute = currentBackStackEntry?.destination?.route
```

**Pass to AppScaffold:**
```kotlin
AppScaffold(
    currentRoute = currentRoute,
    onNavigateBack = { navController.navigateUp() },
    // ... existing parameters
)
```

### 3. Remove Duplicate Titles from Screens

**HomeScreen.kt** (line ~107-117)
- Remove the "SUMMARIZE AI" Text composable
- Remove associated padding modifier if needed

**HistoryScreen.kt** (line ~58-67)
- Remove the "HISTORY" Text composable

**SavedScreen.kt** (line ~58-66)
- Remove the "SAVED" Text composable

**SettingsScreen.kt** (line ~59-68)
- Remove the "SETTINGS" Text composable
- Adjust bottom padding if needed

**OutputScreen.kt** (line ~68-94)
- Remove the entire custom header Row containing:
  - IconButton with back arrow
  - Spacer
  - "SUMMARY" Text
- Screen content will start directly with the summary display

**StreamingOutputScreen.kt**
- Check if there's a title and remove it if present

### 4. Adjust Screen Padding

After removing titles, screens may need padding adjustments:
- Root Column may still need `.statusBarsPadding()` removed (since Scaffold handles it)
- Or Scaffold's `innerPadding` needs to be consumed by content
- Verify visual spacing looks correct

### 5. Handle Scaffold innerPadding

In AppScaffold.kt:
- Currently uses `_` placeholder for innerPadding
- Update NutshellNavHost to receive and apply innerPadding
- This ensures content doesn't overlap with TopAppBar

### 6. Testing Checklist

**Functional Testing:**
- [ ] Home screen shows "HOME" title, no back button
- [ ] History screen shows "HISTORY" title, no back button
- [ ] Saved screen shows "SAVED" title, no back button
- [ ] Settings screen shows "SETTINGS" title, no back button
- [ ] Output screen shows "SUMMARY" title WITH back button
- [ ] StreamingOutput screen shows "SUMMARY" title WITH back button
- [ ] WebPreview screen shows "WEB PREVIEW" title WITH back button
- [ ] Loading screen shows "LOADING" title WITH back button
- [ ] Back button navigates correctly on Output/StreamingOutput screens
- [ ] Bottom navigation still works correctly

**Visual Testing:**
- [ ] No duplicate titles visible
- [ ] Proper spacing between TopAppBar and content
- [ ] Status bar padding handled correctly
- [ ] Material3 styling looks consistent
- [ ] No content overlap with TopAppBar

**Build Testing:**
- [ ] `./gradlew assembleDebug` succeeds
- [ ] No compilation errors
- [ ] No preview errors

## Architecture Compliance

This implementation maintains the app's architectural patterns:
- ✅ Pure composables - screens remain stateless
- ✅ State management in MainActivity - current route tracked at MainActivity level
- ✅ Callbacks passed down - onNavigateBack passed as parameter
- ✅ Clean Architecture with Scaffold Pattern - centralized UI element at outer scaffold
- ✅ No ViewModel injection in screens - navigation state handled in MainActivity

## Files to Modify

1. `/app/src/main/java/com/nutshell/ui/navigation/AppScaffold.kt` - Add TopAppBar
2. `/app/src/main/java/com/nutshell/MainActivity.kt` - Track and pass current route
3. `/app/src/main/java/com/nutshell/ui/screens/home/HomeScreen.kt` - Remove title
4. `/app/src/main/java/com/nutshell/ui/screens/history/HistoryScreen.kt` - Remove title
5. `/app/src/main/java/com/nutshell/ui/screens/saved/SavedScreen.kt` - Remove title
6. `/app/src/main/java/com/nutshell/ui/screens/settings/SettingsScreen.kt` - Remove title
7. `/app/src/main/java/com/nutshell/ui/screens/output/OutputScreen.kt` - Remove custom header
8. `/app/src/main/java/com/nutshell/ui/screens/output/StreamingOutputScreen.kt` - Check and remove title

## Implementation Order

1. Add TopAppBar to AppScaffold.kt with route tracking
2. Update MainActivity.kt to track and pass current route
3. Test that titles appear correctly (with duplicate titles still visible)
4. Remove duplicate titles from all screens
5. Adjust padding and spacing as needed
6. Run full build and manual testing
7. Verify all screens and navigation flows work correctly

## Notes

- Material3 TopAppBar uses `TopAppBarDefaults.smallTopAppBarColors()` by default
- Title style will be smaller than current (22.sp vs 32.sp) per Material3 guidelines
- Back button uses `Icons.AutoMirrored.Filled.ArrowBack` for RTL support
- StreamingOutput route contains parameter: `streaming_output/{inputText}` - need to extract base route
- Consider adding contentDescription for back button accessibility

## Future Enhancements

- Add actions to TopAppBar (e.g., share, delete for Output screen)
- Add scroll behavior (enter/exit animations)
- Add custom colors/styling while maintaining Material3 patterns
- Add elevation or custom dividers if needed
