package com.summarizeai.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.summarizeai.data.model.SummaryData
import com.summarizeai.presentation.viewmodel.HistoryViewModel
import com.summarizeai.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        // Top Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = Elevation.sm,
                    shape = RoundedCornerShape(
                        bottomStart = CornerRadius.lg,
                        bottomEnd = CornerRadius.lg
                    ),
                    ambientColor = ShadowColor,
                    spotColor = ShadowColor
                ),
            colors = CardDefaults.cardColors(containerColor = White),
            shape = RoundedCornerShape(
                bottomStart = CornerRadius.lg,
                bottomEnd = CornerRadius.lg
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.lg)
            ) {
                // Title
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Gray900,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Search history...",
                            color = Gray400
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Gray400,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Cyan600,
                        unfocusedBorderColor = Gray200,
                        focusedContainerColor = Gray50,
                        unfocusedContainerColor = Gray50
                    ),
                    shape = RoundedCornerShape(CornerRadius.md),
                    singleLine = true
                )
            }
        }
        
        // Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray50)
                .padding(Spacing.xl)
        ) {
            if (uiState.filteredSummaries.isEmpty()) {
                // Empty State
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Gray100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "No History",
                            tint = Gray400,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    
                    Text(
                        text = "No History Yet",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Gray900,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "Your summarized texts will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray500,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                // History Items List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(
                        items = uiState.filteredSummaries,
                        key = { it.id }
                    ) { item ->
                        HistoryItemCard(
                            item = item,
                            onDelete = viewModel::deleteSummary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    item: SummaryData,
    onDelete: (SummaryData) -> Unit
) {
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
                .padding(Spacing.lg),
            verticalAlignment = Alignment.Top
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(CornerRadius.md))
                    .background(Cyan50),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History Item",
                    tint = Cyan600,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing.md))
            
            // Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = Spacing.xs)
            ) {
                Text(
                    text = item.mediumSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray800,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                Text(
                    text = formatDate(item.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing.sm))
            
            // Delete Button
            IconButton(
                onClick = { onDelete(item) },
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(CornerRadius.sm))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Gray400,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun formatDate(date: Date): String {
    val now = Date()
    val diffInMinutes = (now.time - date.time) / (1000 * 60)
    
    return when {
        diffInMinutes < 1 -> "Just now"
        diffInMinutes < 60 -> "${diffInMinutes}m ago"
        diffInMinutes < 1440 -> "${diffInMinutes / 60}h ago"
        else -> "${diffInMinutes / 1440}d ago"
    }
}