# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This is a modern Android application that uses AI to summarize text content. It implements **Clean Architecture with Scaffold Pattern** - a custom architectural approach that centralizes all state management at the MainActivity level while keeping screen composables pure and testable.

## Build & Test Commands

```bash
# Build
./gradlew assembleDebug         # Debug APK
./gradlew assembleRelease       # Release APK (ProGuard enabled)
./gradlew bundleRelease         # App Bundle for Play Store

# Testing
./gradlew test                  # Run all unit tests
./gradlew testDebugUnitTest     # Debug unit tests only
./gradlew connectedAndroidTest  # Run instrumentation tests (requires device/emulator)
./gradlew check                 # Run all tests with linting
./gradlew test --info           # Verbose test output

# Development
./gradlew clean                 # Clean build artifacts
./gradlew lint                  # Run lint checks
```

## Architecture: Clean Architecture with Scaffold Pattern

### Core Principle: Centralized State Management

**CRITICAL**: All 7 ViewModels are instantiated and observed at the MainActivity level. Screen composables are pure functions that receive state and callbacks as parameters. This is not standard Compose architecture - do not inject ViewModels into screen composables.

### Architecture Flow

```
MainActivity (Entry Point)
├── Instantiate ALL 7 ViewModels with hiltViewModel()
├── Collect ALL StateFlows with collectAsStateWithLifecycle()
├── Handle ALL navigation logic in LaunchedEffect blocks
└── AppScaffold
    └── NutshellNavHost
        └── MainScreenWithBottomNavigation (Inner Scaffold)
            └── Screen Composables (pure functions, no ViewModels)
```

### The 7 ViewModels

All instantiated in MainActivity, never in screen composables:

1. **HomeViewModel** - Text input, file upload, summarization trigger
2. **OutputViewModel** - Display traditional summary results
3. **StreamingOutputViewModel** - Real-time streaming summary display
4. **HistoryViewModel** - Past summaries management
5. **SavedViewModel** - Bookmarked summaries
6. **SettingsViewModel** - App preferences (streaming toggle, etc.)
7. **WebContentViewModel** - Web content extraction from shared URLs

### Data Flow Pattern

**Traditional Summarization:**
```
HomeScreen → HomeViewModel → SummaryRepository → RemoteDataSource (Retrofit)
                                               → LocalDataSource (Room DB)
OutputScreen ← OutputViewModel ← SummaryRepository (reads from Room DB)
```

**Streaming Summarization:**
```
HomeScreen → HomeViewModel → StreamingSummarizerService (Ktor SSE)
                          ↓
StreamingOutputViewModel ← Flow<StreamChunk>
                          ↓
StreamingOutputScreen (letter-by-letter typing effect, 30ms delay)
```

**Web Content Extraction:**
```
Share Intent → WebContentViewModel → WebContentRepository → WebContentExtractor
                                                            (Three-tier fallback:
                                                             1. Jina AI (fast, handles JS)
                                                             2. Readability4j (Mozilla algorithm)
                                                             3. Jsoup (basic extraction))
HomeScreen (extractedContent pre-filled) → Summarize
```

### Layer Structure

```
app/src/main/java/com/nutshell/
├── MainActivity.kt                    # SINGLE SOURCE OF TRUTH - all ViewModels here
├── ui/
│   ├── navigation/
│   │   ├── AppScaffold.kt            # Outer Material3 scaffold
│   │   ├── NutshellNavHost.kt        # Top-level navigation
│   │   └── Screen.kt                 # Route definitions
│   └── screens/                      # PURE COMPOSABLES - no hiltViewModel() calls
│       ├── home/HomeScreen.kt
│       ├── output/OutputScreen.kt & StreamingOutputScreen.kt
│       ├── history/HistoryScreen.kt
│       ├── saved/SavedScreen.kt
│       ├── settings/SettingsScreen.kt
│       └── webpreview/WebPreviewScreen.kt
├── presentation/viewmodel/           # State management (7 ViewModels)
├── domain/repository/                # Repository interfaces
├── data/
│   ├── repository/                   # Repository implementations
│   ├── local/                        # Room database, DataStore
│   │   ├── database/NutshellDatabase.kt, SummaryDao.kt, SummaryEntity.kt
│   │   └── preferences/UserPreferences.kt
│   ├── remote/                       # Retrofit + Ktor
│   │   ├── api/SummarizerApi.kt, StreamingSummarizerService.kt
│   │   └── extractor/WebContentExtractor.kt
│   └── model/                        # Data models
└── di/                               # Hilt modules (DatabaseModule, RepositoryModule, ApiModule)
```

## Critical Architectural Rules

### DO's ✅

1. **Instantiate all ViewModels in MainActivity** - Use `hiltViewModel()` only in MainActivity
2. **Pass state and callbacks down** - Screen composables receive state as parameters
3. **Handle navigation in MainActivity** - Use `LaunchedEffect(state)` blocks to watch state and navigate
4. **Write pure composables** - Screens should be testable with mock states
5. **Use optimistic UI updates** - Update local state immediately, sync with backend asynchronously
6. **Write @Preview for all composables** - Enabled by pure composable pattern

### DON'Ts ❌

1. **Don't use hiltViewModel() in screen composables** - Breaks the architecture pattern
2. **Don't handle navigation in screens** - Navigation logic belongs in MainActivity
3. **Don't scatter state management** - Single source of truth at MainActivity level
4. **Don't create fragments** - Single Activity + Compose Navigation only

## Example Patterns

### MainActivity Pattern

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // ✅ INSTANTIATE ALL VIEWMODELS HERE
            val homeViewModel: HomeViewModel = hiltViewModel()
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            // ... all 7 ViewModels

            // ✅ COLLECT ALL STATE FLOWS HERE
            val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
            val isStreamingEnabled by settingsViewModel.isStreamingEnabled.collectAsStateWithLifecycle()

            // ✅ HANDLE NAVIGATION HERE
            LaunchedEffect(homeUiState.shouldNavigateToStreaming) {
                if (homeUiState.shouldNavigateToStreaming) {
                    navController.navigate(Screen.StreamingOutput.route)
                    homeViewModel.clearNavigationFlags()
                }
            }

            // ✅ PASS STATE AND CALLBACKS DOWN
            AppScaffold(
                homeUiState = homeUiState,
                onSummarize = { homeViewModel.summarizeText() }
            )
        }
    }
}
```

### Screen Composable Pattern

```kotlin
// ✅ PURE COMPOSABLE - No ViewModel injection
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    extractedContent: String? = null,
    onUpdateTextInput: (String) -> Unit,
    onSummarizeText: () -> Unit,
    onUploadFile: (Uri) -> Unit
) {
    // Use state directly, call callbacks for events
    TextField(
        value = uiState.textInput,
        onValueChange = onUpdateTextInput
    )

    Button(
        onClick = onSummarizeText,
        enabled = uiState.isSummarizeEnabled && !uiState.isLoading
    ) {
        Text("Summarize")
    }
}

// ✅ PREVIEW ENABLED - Works because no ViewModel dependency
@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        uiState = HomeUiState(textInput = "Sample text"),
        onUpdateTextInput = {},
        onSummarizeText = {},
        onUploadFile = {}
    )
}
```

## Technology Stack

### Core Android
- **Jetpack Compose BOM 2024.02.00** - Declarative UI
- **Material 3** - Latest design system
- **Navigation Compose 2.7.6** - Type-safe navigation
- **Lifecycle Compose 2.7.0** - Lifecycle-aware state collection

### State & DI
- **Hilt 2.48** - Dependency injection
- **Coroutines & Flow** - Async programming with StateFlow/SharedFlow
- **DataStore Preferences 1.0.0** - User preferences
- **ViewModel Compose 2.7.0** - State management

### Data Layer
- **Room 2.6.1** - Local database (single source of truth)
- **Retrofit 2.9.0** - REST API client
- **Ktor 2.3.7** - SSE (Server-Sent Events) streaming client
- **OkHttp 4.12.0** - HTTP client with logging interceptor

### Content Processing
- **PDFBox Android 2.0.27.0** - PDF text extraction
- **Jsoup 1.17.2** - HTML parsing
- **Readability4j 1.0.8** - Mozilla Readability algorithm for article extraction
- **Coil 2.5.0** - Image loading

### Testing
- **JUnit 4.13.2** - Unit testing
- **Mockito Kotlin 5.2.1** - Mocking
- **Robolectric 4.11.1** - Android framework mocking for unit tests
- **MockK 1.13.8** - Kotlin mocking
- **Espresso 3.5.1** - UI testing
- **Compose UI Test** - Compose testing utilities

## Key Implementation Details

### Dual Summarization Strategy

The app supports two summarization modes (toggle in Settings):

1. **Traditional API** (Retrofit) - Single request/response, entire summary at once
2. **Streaming API** (Ktor SSE) - Real-time token streaming with typing effect

Both save results to Room database for offline access.

### Web Content Extraction Pipeline

When a URL is shared to the app:

1. **Validation** - Check HTTP/HTTPS only, verify network connectivity
2. **Extraction** - Use three-tier fallback approach:
   - **Tier 1: Jina AI** - Fast, handles JavaScript-rendered content, most reliable (20s timeout)
   - **Tier 2: Readability4j** - Mozilla Readability algorithm for better article parsing (30s timeout)
   - **Tier 3: Jsoup** - Basic HTML extraction with enhanced meta tags and content selectors (30s timeout)
   - System tries each method in order until successful extraction
3. **Pre-fill** - Extracted content auto-populates HomeScreen input
4. **Summarize** - User can trigger summarization on extracted content

### File Processing Error Handling

PDF processing has comprehensive error handling for:
- Password-protected PDFs
- Corrupted files
- Glyph list errors
- Memory issues
- Provides user-friendly error messages instead of crashing

### Navigation Architecture

Dual scaffold pattern:
- **Outer Scaffold** (AppScaffold) - Top-level Material3 wrapper
- **Inner Scaffold** (MainScreenWithBottomNavigation) - Bottom navigation bar with nested NavHost

### Repository Pattern

All repositories follow offline-first pattern:
1. Fetch from remote API
2. Save immediately to Room database
3. UI reads from Room database only
4. Room DB is single source of truth

### Testing Strategy

- **Unit Tests** (10 tests) - ViewModels, repositories, services, utilities
- **Instrumentation Tests** (6 tests) - UI components, navigation flows, E2E scenarios
- Follow **Arrange-Act-Assert** pattern
- Use **Robolectric** for Android framework mocking in unit tests
- Write tests after completing UI/flow (per Cursor rules)

## Code Style Guidelines

From `.cursor/rules/android.mdc`:

### Naming Conventions
- **PascalCase** - Classes
- **camelCase** - Variables, functions, methods
- **underscores_case** - File and directory names
- **UPPERCASE** - Environment variables and constants
- **Verbs for booleans** - isLoading, hasError, canDelete
- **Descriptive names** - Use complete words (user instructions override)

### Function Guidelines
- Write short functions (<20 instructions)
- Single purpose per function
- Start function names with verbs
- Use early returns to avoid nesting
- Use higher-order functions (map, filter, reduce)
- Single level of abstraction

### Class Guidelines
- Follow **SOLID principles**
- Prefer composition over inheritance
- Small classes (<200 instructions, <10 public methods, <10 properties)
- Use data classes for data structures
- Prefer immutability (val over var)

### Testing Guidelines
- **Arrange-Act-Assert** convention
- Name test variables: inputX, mockX, actualX, expectedX
- Write unit tests for each public function
- Use test doubles for dependencies
- Write tests after completing UI/flow

## Configuration

### Build Configuration
- **Gradle Plugin**: 8.2.2
- **Kotlin**: 1.9.22
- **Compile SDK**: 34 (Android 14)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34
- **Java Version**: 17
- **Compose Compiler**: 1.5.8

### Build Variants
- **Debug** - Debuggable, `.debug` applicationIdSuffix, `-debug` versionNameSuffix
- **Release** - Minify enabled, shrinkResources enabled, ProGuard optimization

### Intent Filters
The app handles URL sharing via:
```xml
<intent-filter>
    <action android:name="android.intent.action.SEND" />
    <category android:name="android.intent.category.DEFAULT" />
    <data android:mimeType="text/plain" />
</intent-filter>
```

## Common Development Patterns

### Adding a New Screen

1. Create pure composable in `ui/screens/newfeature/NewFeatureScreen.kt`
2. Create ViewModel in `presentation/viewmodel/NewFeatureViewModel.kt`
3. Add route to `ui/navigation/Screen.kt`
4. Instantiate ViewModel in MainActivity with `hiltViewModel()`
5. Collect state in MainActivity with `collectAsStateWithLifecycle()`
6. Add navigation logic in MainActivity LaunchedEffect
7. Add screen to NavHost in `NutshellNavHost.kt`
8. Pass state and callbacks to screen composable
9. Write @Preview for the screen
10. Write unit tests after completing the flow

### Adding a New API Endpoint

1. Add interface method to `data/remote/api/SummarizerApi.kt` (Retrofit) or update `StreamingSummarizerService.kt` (Ktor)
2. Add data model to `data/model/`
3. Update repository interface in `domain/repository/`
4. Implement in `data/repository/` implementation class
5. Update ViewModel to call repository method
6. Handle result in UI state
7. Write unit tests for repository and ViewModel

### Adding a New Database Entity

1. Create entity in `data/local/database/SummaryEntity.kt`
2. Add DAO methods to `data/local/database/SummaryDao.kt`
3. Update Room database version in `NutshellDatabase.kt`
4. Add migration if needed
5. Create mapper in `data/local/mapper/` for Entity ↔ Model conversion
6. Use in repository implementation
7. Write unit tests with Room in-memory database

## Important Notes

### Streaming Implementation
- Uses Ktor SSE client with Flow<StreamChunk>
- Letter-by-letter rendering with 30ms delay for smooth animation
- Cancellable with proper coroutine lifecycle management
- Saves to Room database on completion

### Web Content Extraction
- Three-tier fallback approach: Jina AI (tier 1, fast & handles JS) → Readability4j (tier 2, Mozilla algorithm) → Jsoup (tier 3, basic extraction)
- Network connectivity check before extraction
- Handles paywall detection and content validation
- Different timeout strategies per tier (20s for Jina AI, 30s for others)
- User-friendly error messages

### Optimistic UI Updates
- History and Saved screens update immediately
- Background Room DB sync happens asynchronously
- Better UX with no loading states for common operations

### Material 3 Theming
- Dynamic color support
- Theme configuration in `ui/theme/`
- Follow Material 3 design guidelines

## Known Limitations

1. **Backend Performance** - Streaming speed depends on AI service latency
2. **Web Extraction** - Some websites block content extraction (paywall, anti-scraping)
3. **PDF Processing** - Large PDFs may cause memory issues on low-end devices
4. **Network Dependency** - Summarization requires internet (no offline AI)

## Future Enhancements

Based on README documentation:
- Repository tests with Room database testing
- ViewModel tests for all 7 ViewModels (currently partial coverage)
- AI Service integration tests
- Performance tests for large datasets
- Accessibility tests (screen reader compliance)
- Target: 80%+ code coverage
- always bump up version number when you push to github
- don't bump up version number if there's no actual code change