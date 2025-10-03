package com.summarizeai.ui.screens.output

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
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
import com.summarizeai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutputScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var isSaved by remember { mutableStateOf(false) }
    
    val tabs = listOf("Short", "Medium", "Detailed")
    val summaries = listOf(
        "This is a short summary of the text that provides key points concisely.",
        "This is a medium-length summary that provides more detail while still being concise. It includes the main points and supporting information in a balanced way.",
        "This is a detailed summary that provides comprehensive coverage of the original text. It includes all major points, supporting details, and context to give the reader a thorough understanding of the content."
    )
    
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
                        imageVector = Icons.Default.ArrowBack,
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
                                    if (selectedTab == index) Gray900 else androidx.compose.ui.graphics.Color.Transparent
                                )
                                .padding(horizontal = Spacing.md),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selectedTab == index) White else Gray600,
                                fontWeight = if (selectedTab == index) FontWeight.Medium else FontWeight.Normal,
                                modifier = Modifier.clickable { selectedTab = index }
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
                    text = summaries[selectedTab],
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
                    // Copy and Save Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        // Copy Button
                        OutlinedButton(
                            onClick = { /* TODO: Implement copy functionality */ },
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
                            onClick = { 
                                isSaved = !isSaved
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isSaved) Cyan600 else Gray900,
                                containerColor = if (isSaved) Cyan50 else White
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(
                                    listOf(
                                        if (isSaved) Cyan600 else Gray200,
                                        if (isSaved) Cyan600 else Gray200
                                    )
                                )
                            ),
                            shape = RoundedCornerShape(CornerRadius.lg)
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save",
                                modifier = Modifier.size(20.dp),
                                tint = if (isSaved) Cyan600 else Gray600
                            )
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text("Save")
                        }
                    }
                    
                    // Share Button
                    Button(
                        onClick = { /* TODO: Implement share functionality */ },
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