# Clean Architecture with Scaffold Pattern

## Overview

This document defines the **Clean Architecture with Scaffold Pattern** - a comprehensive approach to Android app architecture using Jetpack Compose that ensures maintainable, testable, and scalable code from the start.

## Core Principles

### 1. **Single Source of Truth**
- All app state is observed at the MainActivity level
- ViewModels are instantiated once and their state flows down through the composable hierarchy

### 2. **Unidirectional Data Flow**
- **State flows DOWN**: From MainActivity → AppScaffold → Navigation → Screens
- **Events flow UP**: From Screens → Navigation → AppScaffold → MainActivity → ViewModels

### 3. **Pure Composables**
- Screen composables have no direct ViewModel dependencies
- All state and callbacks are passed as parameters
- Screens become testable and reusable

### 4. **Centralized Navigation**
- All navigation logic is handled in MainActivity
- Navigation decisions are based on ViewModel state changes
- Navigation flags are managed centrally

## Architecture Structure

```
MainActivity (Entry Point)
├── Observe ALL ViewModels
├── Collect ALL State Flows
├── Handle ALL Navigation Logic
└── AppScaffold
    ├── Material3 Scaffold Wrapper
    └── SummarizeAINavHost
        ├── MainScreenWithBottomNavigation (Inner Scaffold)
        │   └── NavHost with Screen Composables
        └── Other Navigation Routes
```

## Implementation Template

### 1. MainActivity Structure

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SummarizeAITheme {
                val navController = rememberNavController()
                
                // 🔑 OBSERVE ALL VIEWMODELS AT TOP LEVEL
                val homeViewModel: HomeViewModel = hiltViewModel()
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                val outputViewModel: OutputViewModel = hiltViewModel()
                val streamingOutputViewModel: StreamingOutputViewModel = hiltViewModel()
                val historyViewModel: HistoryViewModel = hiltViewModel()
                val savedViewModel: SavedViewModel = hiltViewModel()
                
                // 🔑 COLLECT ALL STATE FLOWS
                val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
                val isStreamingEnabled by settingsViewModel.isStreamingEnabled.collectAsStateWithLifecycle(initialValue = true)
                val outputUiState by outputViewModel.uiState.collectAsStateWithLifecycle()
                val streamingOutputUiState by streamingOutputViewModel.uiState.collectAsStateWithLifecycle()
                val historyUiState by historyViewModel.uiState.collectAsStateWithLifecycle()
                val historySearchQuery by historyViewModel.searchQuery.collectAsStateWithLifecycle()
                val savedUiState by savedViewModel.uiState.collectAsStateWithLifecycle()
                val savedSearchQuery by savedViewModel.searchQuery.collectAsStateWithLifecycle()
                
                // 🔑 HANDLE ALL NAVIGATION LOGIC HERE
                LaunchedEffect(homeUiState) {
                    if (homeUiState.shouldNavigateToStreaming) {
                        navController.navigate(Screen.StreamingOutput.createRoute(homeUiState.textInput))
                        homeViewModel.clearNavigationFlags()
                    } else if (homeUiState.shouldNavigateToOutput) {
                        navController.navigate(Screen.Output.route)
                        homeViewModel.clearNavigationFlags()
                    }
                }
                
                // 🔑 PASS ALL STATE AND CALLBACKS DOWN
                AppScaffold(
                    navController = navController,
                    homeUiState = homeUiState,
                    isStreamingEnabled = isStreamingEnabled,
                    outputUiState = outputUiState,
                    streamingOutputUiState = streamingOutputUiState,
                    historyUiState = historyUiState,
                    historySearchQuery = historySearchQuery,
                    savedUiState = savedUiState,
                    savedSearchQuery = savedSearchQuery,
                    homeViewModel = homeViewModel,
                    settingsViewModel = settingsViewModel,
                    outputViewModel = outputViewModel,
                    streamingOutputViewModel = streamingOutputViewModel,
                    historyViewModel = historyViewModel,
                    savedViewModel = savedViewModel
                )
            }
        }
    }
}
```

### 2. AppScaffold Composable

```kotlin
@Composable
fun AppScaffold(
    navController: NavHostController,
    homeUiState: HomeUiState,
    isStreamingEnabled: Boolean,
    outputUiState: OutputUiState,
    streamingOutputUiState: StreamingOutputUiState,
    historyUiState: HistoryUiState,
    historySearchQuery: String,
    savedUiState: SavedUiState,
    savedSearchQuery: String,
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    outputViewModel: OutputViewModel,
    streamingOutputViewModel: StreamingOutputViewModel,
    historyViewModel: HistoryViewModel,
    savedViewModel: SavedViewModel
) {
    // 🔑 MATERIAL3 SCAFFOLD WRAPPER
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { _ ->
        SummarizeAINavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize(),
            // 🔑 PASS THROUGH ALL STATE AND CALLBACKS
            homeUiState = homeUiState,
            isStreamingEnabled = isStreamingEnabled,
            outputUiState = outputUiState,
            streamingOutputUiState = streamingOutputUiState,
            historyUiState = historyUiState,
            historySearchQuery = historySearchQuery,
            savedUiState = savedUiState,
            savedSearchQuery = savedSearchQuery,
            homeViewModel = homeViewModel,
            settingsViewModel = settingsViewModel,
            outputViewModel = outputViewModel,
            streamingOutputViewModel = streamingOutputViewModel,
            historyViewModel = historyViewModel,
            savedViewModel = savedViewModel
        )
    }
}
```

### 3. Navigation Host Structure

```kotlin
@Composable
fun SummarizeAINavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    // 🔑 ACCEPT ALL STATE AND CALLBACKS
    homeUiState: HomeUiState,
    isStreamingEnabled: Boolean,
    outputUiState: OutputUiState,
    streamingOutputUiState: StreamingOutputUiState,
    historyUiState: HistoryUiState,
    historySearchQuery: String,
    savedUiState: SavedUiState,
    savedSearchQuery: String,
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    outputViewModel: OutputViewModel,
    streamingOutputViewModel: StreamingOutputViewModel,
    historyViewModel: HistoryViewModel,
    savedViewModel: SavedViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Main.route) {
            MainScreenWithBottomNavigation(
                navController = navController,
                // 🔑 PASS THROUGH TO INNER NAVIGATION
                homeUiState = homeUiState,
                isStreamingEnabled = isStreamingEnabled,
                outputUiState = outputUiState,
                streamingOutputUiState = streamingOutputUiState,
                historyUiState = historyUiState,
                historySearchQuery = historySearchQuery,
                savedUiState = savedUiState,
                savedSearchQuery = savedSearchQuery,
                homeViewModel = homeViewModel,
                settingsViewModel = settingsViewModel,
                outputViewModel = outputViewModel,
                streamingOutputViewModel = streamingOutputViewModel,
                historyViewModel = historyViewModel,
                savedViewModel = savedViewModel
            )
        }
    }
}

@Composable
fun MainScreenWithBottomNavigation(
    navController: NavHostController,
    // 🔑 ACCEPT ALL STATE AND CALLBACKS
    homeUiState: HomeUiState,
    isStreamingEnabled: Boolean,
    outputUiState: OutputUiState,
    streamingOutputUiState: StreamingOutputUiState,
    historyUiState: HistoryUiState,
    historySearchQuery: String,
    savedUiState: SavedUiState,
    savedSearchQuery: String,
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    outputViewModel: OutputViewModel,
    streamingOutputViewModel: StreamingOutputViewModel,
    historyViewModel: HistoryViewModel,
    savedViewModel: SavedViewModel
) {
    val bottomNavController = rememberNavController()
    
    // 🔑 INNER SCAFFOLD FOR BOTTOM NAVIGATION
    Scaffold(
        bottomBar = { /* Bottom Navigation Bar */ }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    // 🔑 PASS STATE AND CALLBACKS TO SCREENS
                    uiState = homeUiState,
                    isStreamingEnabled = isStreamingEnabled,
                    onUpdateTextInput = homeViewModel::updateTextInput,
                    onSummarizeText = homeViewModel::summarizeText,
                    onClearError = homeViewModel::clearError,
                    onResetState = homeViewModel::resetState,
                    onClearNavigationFlags = homeViewModel::clearNavigationFlags,
                    onUploadFile = homeViewModel::uploadFile
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    isStreamingEnabled = isStreamingEnabled,
                    onSetStreamingEnabled = settingsViewModel::setStreamingEnabled
                )
            }
            
            // ... other screens
        }
    }
}
```

### 4. Screen Composable Pattern

```kotlin
// ❌ WRONG - Direct ViewModel Usage
@Composable
fun HomeScreen(
    onNavigateToLoading: () -> Unit,
    onNavigateToOutput: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel() // ❌ DON'T DO THIS
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle() // ❌ DON'T DO THIS
    
    // Screen implementation
}

// ✅ CORRECT - Pure Composable with State and Callbacks
@Composable
fun HomeScreen(
    onNavigateToLoading: () -> Unit,
    onNavigateToOutput: () -> Unit,
    uiState: HomeUiState, // ✅ Accept pre-observed state
    isStreamingEnabled: Boolean,
    onUpdateTextInput: (String) -> Unit, // ✅ Accept callback functions
    onSummarizeText: () -> Unit,
    onClearError: () -> Unit,
    onResetState: () -> Unit,
    onClearNavigationFlags: () -> Unit,
    onUploadFile: (Uri) -> Unit
) {
    // ✅ Use state directly, call callbacks for events
    BasicTextField(
        value = uiState.textInput,
        onValueChange = onUpdateTextInput // ✅ Use callback
    )
    
    Button(
        onClick = {
            if (uiState.textInput.isNotBlank()) {
                onSummarizeText() // ✅ Use callback
            }
        },
        enabled = uiState.isSummarizeEnabled && !uiState.isLoading
    ) {
        Text("Summarize")
    }
    
    // ✅ Handle extracted content from shared URL
    LaunchedEffect(extractedContent) {
        extractedContent?.let { content ->
            if (content.isNotBlank()) {
                onUpdateTextInput(content) // ✅ Use callback
                onSummarizeText() // ✅ Use callback
            }
        }
    }
}
```

## Key Rules to Follow

### ✅ **DO's**

1. **Observe ViewModels at MainActivity Level**
   ```kotlin
   val homeViewModel: HomeViewModel = hiltViewModel()
   val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
   ```

2. **Pass State and Callbacks Down**
   ```kotlin
   ScreenComposable(
       uiState = homeUiState,
       onAction = homeViewModel::performAction
   )
   ```

3. **Handle Navigation in MainActivity**
   ```kotlin
   LaunchedEffect(homeUiState) {
       if (homeUiState.shouldNavigate) {
           navController.navigate(destination)
           homeViewModel.clearNavigationFlags()
       }
   }
   ```

4. **Use Pure Composables**
   ```kotlin
   @Composable
   fun Screen(uiState: UiState, onAction: () -> Unit) {
       // Pure composable logic
   }
   ```

### ❌ **DON'Ts**

1. **Don't Use ViewModels in Screen Composables**
   ```kotlin
   // ❌ WRONG
   @Composable
   fun Screen(viewModel: SomeViewModel = hiltViewModel()) {
       val uiState by viewModel.uiState.collectAsStateWithLifecycle()
   }
   ```

2. **Don't Handle Navigation in Screens**
   ```kotlin
   // ❌ WRONG
   @Composable
   fun Screen() {
       LaunchedEffect(uiState) {
           if (uiState.shouldNavigate) {
               navController.navigate(destination)
           }
       }
   }
   ```

3. **Don't Scatter State Management**
   ```kotlin
   // ❌ WRONG - Multiple ViewModel instances
   val viewModel1: HomeViewModel = hiltViewModel()
   val viewModel2: HomeViewModel = hiltViewModel() // Different instance!
   ```

## Benefits

### 🎯 **Maintainability**
- Single source of truth for all app state
- Clear separation of concerns
- Predictable state flow

### 🧪 **Testability**
- Pure composables are easy to test
- Mock state and callbacks for unit tests
- No ViewModel dependencies in UI tests

### 🔄 **Reusability**
- Screens can be reused in different contexts
- Preview composables work easily
- Components are more modular

### 🚀 **Performance**
- Optimized state collection
- Reduced recomposition
- Better memory management

### 🛠️ **Developer Experience**
- Clear architecture patterns
- Easy to understand and modify
- Consistent code structure

## Migration Guide

If you need to migrate from scattered ViewModel usage to this pattern:

1. **Move ViewModel instantiation to MainActivity**
2. **Collect all state flows at MainActivity level**
3. **Update screen signatures to accept state and callbacks**
4. **Remove `hiltViewModel()` calls from screens**
5. **Move navigation logic to MainActivity**
6. **Pass state and callbacks through the hierarchy**
7. **Test thoroughly after migration**

## File Structure

```
app/src/main/java/com/yourpackage/
├── MainActivity.kt (ViewModel observation, state collection, navigation logic)
├── ui/
│   ├── navigation/
│   │   └── SummarizeAINavHost.kt (Navigation setup, state passing)
│   └── screens/
│       ├── home/
│       │   └── HomeScreen.kt (Pure composable, state + callbacks)
│       ├── settings/
│       │   └── SettingsScreen.kt (Pure composable, state + callbacks)
│       └── ... (Other screens follow same pattern)
└── presentation/
    └── viewmodel/
        ├── HomeViewModel.kt
        ├── SettingsViewModel.kt
        └── ... (ViewModels remain unchanged)
```

## Conclusion

This Clean Architecture with Scaffold Pattern ensures your Android app is:
- **Maintainable** - Clear structure and separation of concerns
- **Testable** - Pure composables with no side effects
- **Scalable** - Easy to add new features and screens
- **Performant** - Optimized state management and recomposition

Follow this pattern from the beginning to avoid costly refactoring later!
