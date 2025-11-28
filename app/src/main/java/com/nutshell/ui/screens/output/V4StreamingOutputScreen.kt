package com.nutshell.ui.screens.output

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutshell.presentation.viewmodel.V4StreamingOutputUiState
import com.nutshell.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun V4StreamingOutputScreen(
    uiState: V4StreamingOutputUiState,
    inputText: String? = null,
    inputUrl: String? = null,
    style: String = "executive",
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onStartStreaming: (String?, String?, String) -> Unit,
    onCopyToClipboard: () -> Unit,
    onShareSummary: () -> Unit,
    onToggleSaveStatus: () -> Unit,
    onResetState: () -> Unit = {},
) {
    // Compute current summary text locally
    val currentSummaryText by remember(uiState) {
        derivedStateOf {
            if (uiState.isStreaming) {
                uiState.streamingText
            } else {
                uiState.displayText
            }
        }
    }

    // Start streaming when screen loads
    LaunchedEffect(inputText, inputUrl) {
        println("V4StreamingOutputScreen: LaunchedEffect triggered")
        if (!inputText.isNullOrBlank() || !inputUrl.isNullOrBlank()) {
            println("V4StreamingOutputScreen: Calling onStartStreaming")
            onResetState() // Reset state for fresh streaming session
            onStartStreaming(inputText, inputUrl, style)
        } else {
            println("V4StreamingOutputScreen: Input is blank, not starting streaming")
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
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Content - Flat Minimalist Design
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Status Indicator - Flat with Fade-Out Animation
            AnimatedVisibility(
                visible = uiState.isStreaming,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Animated typing indicator
                    TypingIndicator()

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Nutshell V4 is generating your summary...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        if (uiState.tokensUsed > 0) {
                            Text(
                                text = "Tokens: ${uiState.tokensUsed}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            // Streaming Summary Content - Flat with Border
            // Scale animation for completion effect
            val contentScale = remember { Animatable(1f) }

            // Trigger subtle scale-up when streaming completes
            LaunchedEffect(uiState.isStreaming) {
                if (!uiState.isStreaming && currentSummaryText.isNotEmpty()) {
                    // Single smooth scale animation: 1.0 → 1.02 → 1.0
                    contentScale.animateTo(
                        targetValue = 1f,
                        animationSpec = keyframes {
                            durationMillis = 600
                            1.0f at 0 using EaseInOutCubic
                            1.02f at 300 using EaseInOutCubic
                            1.0f at 600 using EaseInOutCubic
                        }
                    )
                }
            }

            AnimatedVisibility(
                visible = currentSummaryText.isNotEmpty() || uiState.isStreaming,
                enter = fadeIn(
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .scale(contentScale.value)
                        .border(
                            width = 2.dp,
                            color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else PureBlack,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item {
                            // Display formatted text with styled sections
                            val annotatedText = buildAnnotatedString {
                                if (currentSummaryText.isEmpty()) {
                                    append("Starting to generate summary...")
                                } else {
                                    // Parse and style the formatted text
                                    val lines = currentSummaryText.split("\n")
                                    var isFirstLine = true
                                    var inKeyPoints = false

                                    for (line in lines) {
                                        when {
                                            // Title (first line, likely bold)
                                            isFirstLine && line.isNotBlank() && !line.startsWith("Key Points:") && !line.contains("|") -> {
                                                withStyle(
                                                    SpanStyle(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 20.sp
                                                    )
                                                ) {
                                                    append(line)
                                                }
                                                append("\n")
                                                isFirstLine = false
                                            }
                                            // "Key Points:" header
                                            line == "Key Points:" -> {
                                                append("\n")
                                                withStyle(
                                                    SpanStyle(
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 16.sp
                                                    )
                                                ) {
                                                    append(line)
                                                }
                                                append("\n")
                                                inKeyPoints = true
                                            }
                                            // Metadata line (contains |)
                                            line.contains("|") -> {
                                                append("\n")
                                                withStyle(
                                                    SpanStyle(
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Light
                                                    )
                                                ) {
                                                    append(line)
                                                }
                                                inKeyPoints = false
                                            }
                                            // Regular lines
                                            line.isNotBlank() -> {
                                                append(line)
                                                append("\n")
                                                if (!inKeyPoints && !isFirstLine) {
                                                    isFirstLine = false
                                                }
                                            }
                                            // Empty lines
                                            else -> {
                                                append("\n")
                                            }
                                        }
                                    }
                                }
                            }

                            Text(
                                text = annotatedText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 28.sp,
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }

            // Completion info
            if (!uiState.isStreaming && uiState.latencyMs != null) {
                Text(
                    text = "Completed in ${uiState.latencyMs.div(1000).format(2)}s • ${uiState.tokensUsed} tokens",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

/**
 * Format double to specified decimal places
 */
private fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)
