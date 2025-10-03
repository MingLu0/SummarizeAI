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
import com.summarizeai.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    var searchText by remember { mutableStateOf("") }
    var historyItems by remember { mutableStateOf(createSampleHistoryItems()) }
    
    // Filter history items based on search
    val filteredItems = historyItems.filter { item ->
        item.summary.contains(searchText, ignoreCase = true) ||
        item.originalText.contains(searchText, ignoreCase = true)
    }
    
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
                    value = searchText,
                    onValueChange = { searchText = it },
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
            if (filteredItems.isEmpty()) {
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
                        items = filteredItems,
                        key = { it.id }
                    ) { item ->
                        HistoryItemCard(
                            item = item,
                            onDelete = { itemToDelete ->
                                historyItems = historyItems.filter { it.id != itemToDelete.id }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    item: HistoryItem,
    onDelete: (HistoryItem) -> Unit
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
                    text = item.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray800,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                Text(
                    text = item.formattedTimestamp,
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

data class HistoryItem(
    val id: String,
    val originalText: String,
    val summary: String,
    val timestamp: Date,
    val formattedTimestamp: String
)

fun createSampleHistoryItems(): List<HistoryItem> {
    val formatter = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    val now = Date()
    
    return listOf(
        HistoryItem(
            id = "1",
            originalText = "This is a sample original text that was summarized...",
            summary = "This is a sample summary of the original text that provides key insights.",
            timestamp = Date(now.time - 3600000), // 1 hour ago
            formattedTimestamp = "1h ago"
        ),
        HistoryItem(
            id = "2", 
            originalText = "Another piece of text that was processed...",
            summary = "Another summary with different content and insights.",
            timestamp = Date(now.time - 7200000), // 2 hours ago
            formattedTimestamp = "2h ago"
        ),
        HistoryItem(
            id = "3",
            originalText = "A third example of text that needed summarization...",
            summary = "A third summary showing the variety of content processed.",
            timestamp = Date(now.time - 86400000), // 1 day ago
            formattedTimestamp = "1d ago"
        )
    )
}