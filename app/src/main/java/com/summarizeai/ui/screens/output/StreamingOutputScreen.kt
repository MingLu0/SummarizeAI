package com.summarizeai.ui.screens.output

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.summarizeai.presentation.viewmodel.StreamingOutputViewModel
import com.summarizeai.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamingOutputScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    inputText: String,
    viewModel: StreamingOutputViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentSummaryText by remember {
        derivedStateOf {
            if (uiState.isStreaming) {
                uiState.streamingText
            } else {
                val summaryData = uiState.summaryData ?: return@derivedStateOf ""
                when (uiState.selectedTabIndex) {
                    0 -> summaryData.shortSummary
                    1 -> summaryData.mediumSummary
                    2 -> summaryData.detailedSummary
                    else -> summaryData.mediumSummary
                }
            }
        }
    }
    
    // Start streaming when screen loads
    LaunchedEffect(inputText) {
        viewModel.startStreaming(inputText)
    }
    
    // Auto-scroll to bottom when new text arrives
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(currentSummaryText) {
        if (currentSummaryText.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(listState.layoutInfo.totalItemsCount)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Generating Summary...",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Gray900
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(CornerRadius.md))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Gray600,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl)
                .padding(top = Spacing.xs, bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = Elevation.sm,
                        shape = RoundedCornerShape(CornerRadius.lg),
                        ambientColor = ShadowColor,
                        spotColor = ShadowColor
                    ),
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(CornerRadius.lg)
            ) {
                if (uiState.isStreaming) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Animated typing indicator
                        TypingIndicator()
                        
                        Spacer(modifier = Modifier.width(Spacing.md))
                        
                        Text(
                            text = "AI is generating your summary...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray700
                        )
                    }
                } else if (uiState.summaryData != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Summary completed!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray700
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            TextButton(onClick = { viewModel.copyToClipboard() }) {
                                Text("Copy")
                            }
                            TextButton(onClick = { viewModel.shareSummary() }) {
                                Text("Share")
                            }
                            TextButton(onClick = { viewModel.toggleSaveStatus() }) {
                                Text(if (uiState.summaryData?.isSaved == true) "Unsave" else "Save")
                            }
                        }
                    }
                }
            }

            // Streaming Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(
                        elevation = Elevation.sm,
                        shape = RoundedCornerShape(CornerRadius.lg),
                        ambientColor = ShadowColor,
                        spotColor = ShadowColor
                    ),
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(CornerRadius.lg)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    item {
                        Text(
                            text = currentSummaryText.ifEmpty { "Starting to generate summary..." },
                            style = MaterialTheme.typography.bodyLarge,
                            color = Gray700,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                        )
                    }
                    
                    // Add typing cursor at the end
                    if (currentSummaryText.isNotEmpty()) {
                        item {
                            TypingCursor()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "typing_alpha"
    )
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val delay = index * 200
            val dotAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = delay, easing = EaseInOut),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_alpha_$index"
            )
            
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Cyan600.copy(alpha = dotAlpha))
            )
        }
    }
}

@Composable
fun TypingCursor() {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )
    
    Text(
        text = "|",
        style = MaterialTheme.typography.bodyLarge,
        color = Cyan600.copy(alpha = alpha),
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true)
@Composable
fun StreamingOutputScreenPreview() {
    SummarizeAITheme {
        StreamingOutputScreen(
            onNavigateBack = {},
            onNavigateToHome = {},
            inputText = "This is a test input for the preview"
        )
    }
}
