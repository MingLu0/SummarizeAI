package com.summarizeai.ui.screens.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.summarizeai.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen() {
    var searchText by remember { mutableStateOf("") }
    var savedItems by remember { mutableStateOf(createSampleSavedItems()) }
    
    // Filter saved items based on search
    val filteredItems = savedItems.filter { item ->
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
                    text = "Saved",
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
                            text = "Search saved items...",
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
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "No Saved Items",
                            tint = Gray400,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    
                    Text(
                        text = "No Saved Items",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Gray900,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "Bookmark summaries to save them here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray500,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                // Saved Items List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(
                        items = filteredItems,
                        key = { it.id }
                    ) { item ->
                        SavedItemCard(
                            item = item,
                            onUnsave = { itemToUnsave ->
                                savedItems = savedItems.filter { it.id != itemToUnsave.id }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SavedItemCard(
    item: SavedItem,
    onUnsave: (SavedItem) -> Unit
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
                    imageVector = Icons.Default.Description,
                    contentDescription = "Saved Item",
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
                    text = item.formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing.sm))
            
            // Unsave Button
            IconButton(
                onClick = { onUnsave(item) },
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(CornerRadius.sm))
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkRemove,
                    contentDescription = "Unsave",
                    tint = Gray600,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

data class SavedItem(
    val id: String,
    val originalText: String,
    val summary: String,
    val savedDate: Date,
    val formattedDate: String
)

fun createSampleSavedItems(): List<SavedItem> {
    val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
    val now = Date()
    
    return listOf(
        SavedItem(
            id = "s1",
            originalText = "This is a saved summary that was bookmarked...",
            summary = "This is a saved summary with important insights that was bookmarked for future reference.",
            savedDate = Date(now.time - 86400000), // 1 day ago
            formattedDate = "Jan 15"
        ),
        SavedItem(
            id = "s2",
            originalText = "Another piece of content that was saved...",
            summary = "Another saved summary with valuable information that the user wanted to keep.",
            savedDate = Date(now.time - 172800000), // 2 days ago
            formattedDate = "Jan 14"
        )
    )
}
