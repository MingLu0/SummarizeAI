package com.nutshell.ui.screens.output

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
            .statusBarsPadding()
    ) {
        // Content - Flat Minimalist Design
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Custom Header with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PureBlack,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "SUMMARY",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp
                    ),
                    color = PureBlack
                )
            }
            // Tab Selector - Flat Design
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = PureBlack,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabs.forEachIndexed { index, tab ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .background(
                                color = if (uiState.selectedTabIndex == index) ElectricLime else PureWhite,
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
                            color = PureBlack
                        )
                    }
                }
            }

            // Summary Content - Flat with Border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(
                        width = 2.dp,
                        color = PureBlack,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(PureWhite)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(
                        text = currentSummaryText.ifEmpty { "No summary available" },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 28.sp
                        ),
                        color = Gray800
                    )
                }
            }

//            // Action Buttons Container
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .shadow(
//                        elevation = Elevation.sm,
//                        shape = RoundedCornerShape(
//                            topStart = CornerRadius.lg,
//                            topEnd = CornerRadius.lg
//                        ),
//                        ambientColor = ShadowColor,
//                        spotColor = ShadowColor
//                    ),
//                colors = CardDefaults.cardColors(containerColor = White),
//                shape = RoundedCornerShape(
//                    topStart = CornerRadius.lg,
//                    topEnd = CornerRadius.lg
//                )
//            ) {
//                Column(
//                    modifier = Modifier.padding(Spacing.xl),
//                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
//                ) {
//                    // Copy, Save, and Home Buttons Row
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
//                    ) {
//                        // Copy Button
//                        OutlinedButton(
//                            onClick = viewModel::copyToClipboard,
//                            modifier = Modifier
//                                .weight(1f)
//                                .height(48.dp),
//                            colors = ButtonDefaults.outlinedButtonColors(
//                                contentColor = Gray900
//                            ),
//                            border = ButtonDefaults.outlinedButtonBorder.copy(
//                                brush = Brush.linearGradient(listOf(Gray200, Gray200))
//                            ),
//                            shape = RoundedCornerShape(CornerRadius.lg)
//                        ) {
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.Center,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.ContentCopy,
//                                    contentDescription = "Copy",
//                                    modifier = Modifier.size(20.dp)
//                                )
//                                Spacer(modifier = Modifier.width(Spacing.sm))
//                                Text("Copy")
//                            }
//                        }
//
//                        // Save Button
//                        OutlinedButton(
//                            onClick = viewModel::toggleSaveStatus,
//                            modifier = Modifier
//                                .weight(1f)
//                                .height(48.dp),
//                            colors = ButtonDefaults.outlinedButtonColors(
//                                contentColor = if (uiState.summaryData?.isSaved == true) Cyan600 else Gray900,
//                                containerColor = if (uiState.summaryData?.isSaved == true) Cyan50 else White
//                            ),
//                            border = ButtonDefaults.outlinedButtonBorder.copy(
//                                brush = Brush.linearGradient(
//                                    listOf(
//                                        if (uiState.summaryData?.isSaved == true) Cyan600 else Gray200,
//                                        if (uiState.summaryData?.isSaved == true) Cyan600 else Gray200
//                                    )
//                                )
//                            ),
//                            shape = RoundedCornerShape(CornerRadius.lg)
//                        ) {
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.Center,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Icon(
//                                    imageVector = if (uiState.summaryData?.isSaved == true) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
//                                    contentDescription = "Save",
//                                    modifier = Modifier.size(20.dp),
//                                    tint = if (uiState.summaryData?.isSaved == true) Cyan600 else Gray600
//                                )
//                                Spacer(modifier = Modifier.width(Spacing.sm))
//                                Text("Save")
//                            }
//                        }
//
//                        // Home Button
//                        OutlinedButton(
//                            onClick = onNavigateToHome,
//                            modifier = Modifier
//                                .weight(1f)
//                                .height(48.dp),
//                            colors = ButtonDefaults.outlinedButtonColors(
//                                contentColor = Gray900
//                            ),
//                            border = ButtonDefaults.outlinedButtonBorder.copy(
//                                brush = Brush.linearGradient(listOf(Gray200, Gray200))
//                            ),
//                            shape = RoundedCornerShape(CornerRadius.lg)
//                        ) {
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.Center,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.Home,
//                                    contentDescription = "Home",
//                                    modifier = Modifier.size(20.dp)
//                                )
//                                Spacer(modifier = Modifier.width(Spacing.sm))
//                                Text("Home")
//                            }
//                        }
//                    }
//
//                    // Share Button
//                    Button(
//                        onClick = viewModel::shareSummary,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(56.dp)
//                            .shadow(
//                                elevation = Elevation.lg,
//                                shape = RoundedCornerShape(CornerRadius.xxl),
//                                ambientColor = AccentShadow,
//                                spotColor = AccentShadow
//                            ),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Cyan600
//                        ),
//                        shape = RoundedCornerShape(CornerRadius.xxl)
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Share,
//                            contentDescription = "Share",
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Spacer(modifier = Modifier.width(Spacing.sm))
//                        Text(
//                            text = "Share",
//                            style = MaterialTheme.typography.labelLarge,
//                            color = White,
//                            fontWeight = FontWeight.Medium
//                        )
//                    }
//                }
//            }
        }
    }
}
