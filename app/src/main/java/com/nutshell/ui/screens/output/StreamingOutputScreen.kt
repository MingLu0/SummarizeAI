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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutshell.data.local.preferences.ThemeMode
import com.nutshell.data.model.SummaryData
import com.nutshell.presentation.viewmodel.StreamingOutputUiState
import com.nutshell.ui.theme.*
import java.util.*
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

    LaunchedEffect(inputText) {
        if (inputText.isNotBlank()) {
            onResetState()
            onStartStreaming(inputText)
        }
    }

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
            .padding(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
        } else if (uiState.summaryData != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .border(
                            width = 2.dp,
                            color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else PureBlack,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onCopyToClipboard() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "COPY",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .border(
                            width = 2.dp,
                            color = if (uiState.summaryData?.isSaved == true)
                                MaterialTheme.colorScheme.primary
                            else if (isSystemInDarkTheme())
                                MaterialTheme.colorScheme.outline
                            else
                                PureBlack,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            if (uiState.summaryData?.isSaved == true)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else
                                MaterialTheme.colorScheme.surface
                        )
                        .clickable { onToggleSaveStatus() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (uiState.summaryData?.isSaved == true)
                                Icons.Default.Bookmark
                            else
                                Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (uiState.summaryData?.isSaved == true)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SAVE",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = if (uiState.summaryData?.isSaved == true)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .border(
                            width = 2.dp,
                            color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else PureBlack,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onNavigateToHome() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HOME",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

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
            }
        }

        if (!uiState.isStreaming && uiState.summaryData != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onShareSummary() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "SHARE SUMMARY",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StreamingOutputScreenPreview() {
    NutshellTheme(themeMode = ThemeMode.LIGHT) {
        StreamingOutputScreen(
            uiState = StreamingOutputUiState(
                isStreaming = true,
                streamingText = "This is a sample streaming text that demonstrates how the content appears as it is being generated by the AI..."
            ),
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

@Preview(showBackground = true, showSystemUi = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StreamingOutputScreenDarkPreview() {
    NutshellTheme(themeMode = ThemeMode.DARK) {
        StreamingOutputScreen(
            uiState = StreamingOutputUiState(
                isStreaming = false,
                summaryData = SummaryData(
                    id = "1",
                    originalText = "Sample input text",
                    shortSummary = "Short summary",
                    mediumSummary = "This is a completed streaming summary that shows how the screen looks after generation is complete.",
                    detailedSummary = "Detailed summary",
                    createdAt = Date(),
                    isSaved = true
                ),
                streamingText = "This is a completed streaming summary that shows how the screen looks after generation is complete.",
                selectedTabIndex = 0
            ),
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
