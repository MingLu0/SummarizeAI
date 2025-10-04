package com.summarizeai.ui.screens.output

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.summarizeai.presentation.viewmodel.OutputViewModel
import com.summarizeai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutputScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: OutputViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val tabs = listOf("Short", "Medium", "Detailed")
    val currentSummaryText = viewModel.getCurrentSummaryText()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Summary",
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
                .padding(top = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {
            // Tab Selector
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.xs)
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(CornerRadius.md))
                                .background(
                                    if (uiState.selectedTabIndex == index) Gray900 else androidx.compose.ui.graphics.Color.Transparent
                                )
                                .padding(horizontal = Spacing.md),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (uiState.selectedTabIndex == index) White else Gray600,
                                fontWeight = if (uiState.selectedTabIndex == index) FontWeight.Medium else FontWeight.Normal,
                                modifier = Modifier.clickable { viewModel.selectTab(index) }
                            )
                        }
                    }
                }
            }
            
            // Summary Card
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
                Text(
                    text = currentSummaryText.ifEmpty { "No summary available" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gray700,
                    modifier = Modifier.padding(Spacing.xl),
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
            }
            
            // Action Buttons Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = Elevation.sm,
                        shape = RoundedCornerShape(
                            topStart = CornerRadius.lg,
                            topEnd = CornerRadius.lg
                        ),
                        ambientColor = ShadowColor,
                        spotColor = ShadowColor
                    ),
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(
                    topStart = CornerRadius.lg,
                    topEnd = CornerRadius.lg
                )
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.xl),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // Copy, Save, and Home Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        // Copy Button
                        OutlinedButton(
                            onClick = viewModel::copyToClipboard,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Gray900
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(Gray200, Gray200))
                            ),
                            shape = RoundedCornerShape(CornerRadius.lg)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text("Copy")
                        }
                        
                        // Save Button
                        OutlinedButton(
                            onClick = viewModel::toggleSaveStatus,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (uiState.summaryData?.isSaved == true) Cyan600 else Gray900,
                                containerColor = if (uiState.summaryData?.isSaved == true) Cyan50 else White
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(
                                    listOf(
                                        if (uiState.summaryData?.isSaved == true) Cyan600 else Gray200,
                                        if (uiState.summaryData?.isSaved == true) Cyan600 else Gray200
                                    )
                                )
                            ),
                            shape = RoundedCornerShape(CornerRadius.lg)
                        ) {
                            Icon(
                                imageVector = if (uiState.summaryData?.isSaved == true) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save",
                                modifier = Modifier.size(20.dp),
                                tint = if (uiState.summaryData?.isSaved == true) Cyan600 else Gray600
                            )
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text("Save")
                        }
                        
                        // Home Button
                        OutlinedButton(
                            onClick = onNavigateToHome,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Gray900
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(Gray200, Gray200))
                            ),
                            shape = RoundedCornerShape(CornerRadius.lg)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text("Home")
                        }
                    }
                    
                    // Share Button
                    Button(
                        onClick = viewModel::shareSummary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = Elevation.lg,
                                shape = RoundedCornerShape(CornerRadius.xxl),
                                ambientColor = AccentShadow,
                                spotColor = AccentShadow
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Cyan600
                        ),
                        shape = RoundedCornerShape(CornerRadius.xxl)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(
                            text = "Share",
                            style = MaterialTheme.typography.labelLarge,
                            color = White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}