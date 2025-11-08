package com.nutshell.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutshell.data.model.SummaryData
import com.nutshell.presentation.viewmodel.HistoryUiState
import com.nutshell.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    searchQuery: String,
    onUpdateSearchQuery: (String) -> Unit,
    onDeleteSummary: (SummaryData) -> Unit
) {
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
            .statusBarsPadding()
    ) {
        // Content - Flat Minimalist Design
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title
            Text(
                text = "HISTORY",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp
                ),
                color = PureBlack
            )
            
            // Search Input - Flat with Border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        width = 2.dp,
                        color = Gray300,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(PureWhite)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Gray400,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onUpdateSearchQuery,
                        textStyle = TextStyle(
                            color = PureBlack,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        cursorBrush = SolidColor(ElectricLime),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    ) { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search history...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Gray400
                            )
                        }
                        innerTextField()
                    }
                }
            }
        
            // Content Area
            if (uiState.filteredSummaries.isEmpty()) {
                // Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(
                                color = Gray100,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "No History",
                            tint = Gray400,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "No History Yet",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PureBlack,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Your summarized texts will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray700,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                // History Items List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.filteredSummaries,
                        key = { it.id }
                    ) { item ->
                        HistoryItemCard(
                            item = item,
                            onDelete = onDeleteSummary
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
    // Flat Item Card with Border
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Gray300,
                shape = RoundedCornerShape(12.dp)
            )
            .background(PureWhite)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon Container
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = ElectricLime.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "History Item",
                tint = PureBlack,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Content Area
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.mediumSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = Gray800,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = formatDate(item.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = Gray700,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Delete Button
        IconButton(
            onClick = { onDelete(item) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = ErrorRed,
                modifier = Modifier.size(20.dp)
            )
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
