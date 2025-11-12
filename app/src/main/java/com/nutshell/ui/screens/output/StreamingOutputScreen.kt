package com.nutshell.ui.screens.output

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutshell.presentation.viewmodel.StreamingOutputUiState
import com.nutshell.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamingOutputScreen(
    uiState: StreamingOutputUiState,
    inputText: String,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onStartStreaming: (String) -> Unit,
    onSelectTab: (Int) -> Unit,
    onCopyToClipboard: () -> Unit,
    onShareSummary: () -> Unit,
    onToggleSaveStatus: () -> Unit,
    onResetState: () -> Unit = {}
) {
    // Compute current summary text locally
    val currentSummaryText by remember(uiState) {
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
        if (inputText.isNotBlank()) {
            onResetState() // Reset state for fresh streaming session
            onStartStreaming(inputText)
        }
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
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Content - Flat Minimalist Design
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Indicator - Flat
            if (uiState.isStreaming) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Animated typing indicator
                    TypingIndicator()

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Nutshell is generating your summary...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
//                // Success state with bounce animation
//                val scale = remember { Animatable(0.8f) }
//                val alpha = remember { Animatable(0f) }
//
//                LaunchedEffect(Unit) {
//                    launch {
//                        scale.animateTo(
//                            targetValue = 1f,
//                            animationSpec = spring(
//                                dampingRatio = Spring.DampingRatioMediumBouncy,
//                                stiffness = Spring.StiffnessMedium
//                            )
//                        )
//                    }
//                    launch {
//                        alpha.animateTo(
//                            targetValue = 1f,
//                            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
//                        )
//                    }
//                }
//
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .scale(scale.value)
//                        .border(
//                            width = 2.dp,
//                            color = SuccessGreen.copy(alpha = alpha.value),
//                            shape = RoundedCornerShape(12.dp)
//                        )
//                        .background(MaterialTheme.colorScheme.surface.copy(alpha = alpha.value))
//                        .padding(20.dp)
//                ) {
//                    Text(
//                        text = "✓ Summary completed!",
//                        style = MaterialTheme.typography.bodyMedium.copy(
//                            fontWeight = FontWeight.Bold
//                        ),
//                        color = SuccessGreen.copy(alpha = alpha.value)
//                    )
//                }

            // Streaming Summary Content - Flat with Border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(
                        width = 2.dp,
                        color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else PureBlack,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = currentSummaryText.ifEmpty { "Starting to generate summary..." },
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 28.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Add typing cursor at the end
//                    if (currentSummaryText.isNotEmpty()) {
//                        item {
//                            TypingCursor()
//                        }
//                    }
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
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = dotAlpha),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
fun TypingCursor() {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    infiniteTransition.animateFloat(
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
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true)
@Composable
fun StreamingOutputScreenPreview() {
    NutshellTheme {
        StreamingOutputScreen(
            uiState = StreamingOutputUiState(),
            inputText = "This is a test input for the preview",
            onNavigateBack = {},
            onNavigateToHome = {},
            onStartStreaming = {},
            onSelectTab = {},
            onCopyToClipboard = {},
            onShareSummary = {},
            onToggleSaveStatus = {}
        )
    }
}
