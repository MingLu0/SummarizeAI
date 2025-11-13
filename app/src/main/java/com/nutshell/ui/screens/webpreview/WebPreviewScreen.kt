package com.nutshell.ui.screens.webpreview

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
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nutshell.presentation.viewmodel.WebPreviewViewModel
import com.nutshell.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebPreviewScreen(
    initialUrl: String,
    onNavigateBack: () -> Unit,
    onProceedToSummarize: (String, String) -> Unit, // title, content
    viewModel: WebPreviewViewModel = hiltViewModel(),
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
            .background(PureWhite),
    ) {
        // Content - Flat Minimalist Design
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Custom Header with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PureBlack,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Web preview",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp,
                    ),
                    color = PureBlack,
                )
            }

            // URL Box - Flat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else PureBlack,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .background(PureWhite)
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = "URL",
                    tint = PureBlack,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = uiState.url,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray800,
                    modifier = Modifier.weight(1f),
                )
            }

            // Content Box - Flat
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(
                        width = 2.dp,
                        color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else PureBlack,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .background(PureWhite)
                    .padding(20.dp),
            ) {
                Text(
                    text = "Content preview",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = PureBlack,
                )
                Spacer(modifier = Modifier.height(16.dp))

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                CircularProgressIndicator(
                                    color = ElectricLime,
                                    strokeWidth = 3.dp,
                                )
                                Text(
                                    text = "Extracting content...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Gray700,
                                )
                            }
                        }
                    }

                    uiState.error != null -> {
                        val errorMessage = uiState.error
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Text(
                                    text = "⚠️",
                                    style = MaterialTheme.typography.headlineLarge,
                                )
                                Text(
                                    text = errorMessage ?: "Unknown error",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Gray700,
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = ElectricLime,
                                            shape = RoundedCornerShape(12.dp),
                                        )
                                        .clickable { viewModel.extractContent() }
                                        .padding(horizontal = 24.dp, vertical = 12.dp),
                                ) {
                                    Text(
                                        text = "Retry",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                        ),
                                        color = PureBlack,
                                    )
                                }
                            }
                        }
                    }

                    uiState.content != null -> {
                        val content = uiState.content
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                        ) {
                            Text(
                                text = content?.title ?: "Untitled",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = PureBlack,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = content?.content ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray800,
                                lineHeight = 22.sp,
                            )
                        }
                    }
                }
            }

            // Action Buttons - Flat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Cancel Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .border(
                            width = 2.dp,
                            color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else PureBlack,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .background(PureWhite)
                        .clickable(onClick = onNavigateBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = PureBlack,
                    )
                }

                // Summarize Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .background(
                            color = if (uiState.content != null && !uiState.isLoading) ElectricLime else ElectricLimeLight,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable(enabled = uiState.content != null && !uiState.isLoading) {
                            uiState.content?.let { content ->
                                onProceedToSummarize(content.title, content.content)
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Summarize →",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        ),
                        color = PureBlack,
                    )
                }
            }
        }
    }
}
