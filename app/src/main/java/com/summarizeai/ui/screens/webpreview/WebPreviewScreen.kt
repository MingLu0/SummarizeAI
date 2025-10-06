package com.summarizeai.ui.screens.webpreview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.summarizeai.presentation.viewmodel.WebPreviewViewModel
import com.summarizeai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebPreviewScreen(
    initialUrl: String,
    onNavigateBack: () -> Unit,
    onProceedToSummarize: (String, String) -> Unit, // title, content
    viewModel: WebPreviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(initialUrl) {
        if (initialUrl.isNotBlank()) {
            viewModel.setUrl(initialUrl)
            viewModel.extractContent()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Web Content Preview",
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
            // URL Card
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
                        .padding(Spacing.xl),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = "URL",
                        tint = Cyan600,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Text(
                        text = uiState.url,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray700,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Content Card
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.xl)
                ) {
                    Text(
                        text = "Content Preview",
                        style = MaterialTheme.typography.titleMedium,
                        color = Gray900,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    
                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                                ) {
                                    CircularProgressIndicator(
                                        color = Cyan600,
                                        strokeWidth = 3.dp
                                    )
                                    Text(
                                        text = "Extracting content...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Gray600
                                    )
                                }
                            }
                        }
                        
                        uiState.error != null -> {
                            val errorMessage = uiState.error
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                                ) {
                                    Text(
                                        text = "⚠️",
                                        style = MaterialTheme.typography.headlineLarge
                                    )
                                    Text(
                                        text = errorMessage ?: "Unknown error",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Gray700
                                    )
                                    Button(
                                        onClick = { viewModel.extractContent() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Cyan600
                                        ),
                                        shape = RoundedCornerShape(CornerRadius.lg)
                                    ) {
                                        Text("Retry")
                                    }
                                }
                            }
                        }
                        
                        uiState.content != null -> {
                            val content = uiState.content
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = content?.title ?: "Untitled",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Gray900,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(Spacing.md))
                                Text(
                                    text = content?.content ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Gray700,
                                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                                )
                            }
                        }
                    }
                }
            }
            
            // Action Buttons
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.xl),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Gray900
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(Gray200, Gray200))
                        ),
                        shape = RoundedCornerShape(CornerRadius.lg)
                    ) {
                        Text("Cancel")
                    }
                    
                    // Summarize Button
                    Button(
                        onClick = {
                            uiState.content?.let { content ->
                                onProceedToSummarize(content.title, content.content)
                            }
                        },
                        enabled = uiState.content != null && !uiState.isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .shadow(
                                elevation = Elevation.lg,
                                shape = RoundedCornerShape(CornerRadius.lg),
                                ambientColor = AccentShadow,
                                spotColor = AccentShadow
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Cyan600
                        ),
                        shape = RoundedCornerShape(CornerRadius.lg)
                    ) {
                        Text(
                            text = "Summarize",
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

