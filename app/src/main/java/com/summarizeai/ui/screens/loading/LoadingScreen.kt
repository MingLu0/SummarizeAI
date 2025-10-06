package com.summarizeai.ui.screens.loading

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.summarizeai.presentation.viewmodel.HomeViewModel
import com.summarizeai.ui.theme.*

@Composable
fun LoadingScreen(
    onNavigateToOutput: () -> Unit = {},
    onNavigateBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Handle navigation based on API call result
    LaunchedEffect(uiState) {
        println("LoadingScreen: LaunchedEffect triggered")
        println("LoadingScreen: summaryData = ${uiState.summaryData}")
        println("LoadingScreen: isLoading = ${uiState.isLoading}")
        println("LoadingScreen: error = ${uiState.error}")
        
        when {
            uiState.summaryData != null && !uiState.isLoading -> {
                println("LoadingScreen: Navigating to output screen")
                onNavigateToOutput()
            }
            uiState.error != null && !uiState.isLoading -> {
                println("LoadingScreen: Navigating back due to error")
                onNavigateBack()
            }
        }
    }
    
    // Add timeout mechanism to prevent infinite loading
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(60000) // 60 seconds timeout
        if (uiState.isLoading) {
            onNavigateBack()
        }
    }
    // Animation for dots
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    
    val dot1Scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    
    val dot2Scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, delayMillis = 200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    
    val dot3Scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, delayMillis = 400, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )
    
    // Progress bar animation
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {
            // Animated Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dot 1
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .scale(dot1Scale)
                        .clip(CircleShape)
                        .background(Cyan600)
                )
                
                // Dot 2
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .scale(dot2Scale)
                        .clip(CircleShape)
                        .background(Cyan600)
                )
                
                // Dot 3
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .scale(dot3Scale)
                        .clip(CircleShape)
                        .background(Cyan600)
                )
            }
            
            // Status Text
            Text(
                text = "Analyzing text...",
                style = MaterialTheme.typography.headlineSmall,
                color = Gray900,
                fontWeight = FontWeight.SemiBold
            )
            
            // Progress Bar
            Box(
                modifier = Modifier
                    .width(240.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(CornerRadius.sm))
                    .background(Gray200)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(240.dp * progress)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Cyan600, Blue600)
                            )
                        )
                )
            }
            
            // Helper Text
            Text(
                text = "Please wait while we summarize your content",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray600,
                modifier = Modifier.widthIn(max = 280.dp)
            )
            
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    SummarizeAITheme {
        LoadingScreen(
            onNavigateToOutput = {},
            onNavigateBack = {}
        )
    }
}