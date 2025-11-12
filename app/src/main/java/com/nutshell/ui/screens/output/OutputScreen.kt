package com.nutshell.ui.screens.output

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutshell.presentation.viewmodel.OutputUiState
import com.nutshell.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutputScreen(
    uiState: OutputUiState,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onSelectTab: (Int) -> Unit,
    onCopyToClipboard: () -> Unit,
    onShareSummary: () -> Unit,
    onToggleSaveStatus: () -> Unit
) {
    val tabs = listOf("Short", "Medium", "Detailed")
    
    // Compute current summary text locally
    val currentSummaryText = remember(uiState.summaryData, uiState.selectedTabIndex) {
        val summaryData = uiState.summaryData ?: return@remember ""
        when (uiState.selectedTabIndex) {
            0 -> summaryData.shortSummary
            1 -> summaryData.mediumSummary
            2 -> summaryData.detailedSummary
            else -> summaryData.mediumSummary
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
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tab Selector - Flat Design with Animated Transitions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else PureBlack,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = uiState.selectedTabIndex == index

                    // Animated background color
                    val backgroundColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        label = "tabBackgroundColor_$index"
                    )

                    // Animated text color
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        label = "tabTextColor_$index"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .background(
                                color = backgroundColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onSelectTab(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = textColor
                        )
                    }
                }
            }

            // Summary Content - Flat with Border and Crossfade Animation
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
                // Use Crossfade for smooth content transition
                Crossfade(
                    targetState = currentSummaryText,
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                    label = "summaryContentCrossfade"
                ) { text ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {
                        Text(
                            text = text.ifEmpty { "No summary available" },
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 28.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Action Buttons - Flat Minimalist Design
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Row: Copy, Save, Home Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Copy Button
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

                    // Save Button (with saved state)
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

                    // Home Button
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

                // Share Button - Full Width Primary CTA
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
