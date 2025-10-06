package com.summarizeai.ui.screens.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.summarizeai.presentation.viewmodel.HomeViewModel
import com.summarizeai.utils.FilePickerUtils
import com.summarizeai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLoading: () -> Unit,
    onNavigateToOutput: () -> Unit,
    extractedContent: String? = null,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Handle extracted content from shared URL
    LaunchedEffect(extractedContent) {
        println("HomeScreen: LaunchedEffect triggered, extractedContent: ${extractedContent?.let { "length ${it.length}" } ?: "null"}")
        extractedContent?.let { content ->
            if (content.isNotBlank()) {
                println("HomeScreen: Received extracted content, length: ${content.length}")
                println("HomeScreen: Setting text input and starting summarization")
                // Set the extracted content and start summarization
                viewModel.updateTextInput(content)
                viewModel.summarizeText()
                println("HomeScreen: Summarization started")
            } else {
                println("HomeScreen: Content is blank, not starting summarization")
            }
        } ?: run {
            println("HomeScreen: No extracted content received")
        }
    }
    
    // Navigate to output screen when API call completes successfully
    LaunchedEffect(uiState) {
        println("HomeScreen: UI state changed - isLoading: ${uiState.isLoading}, summaryData: ${uiState.summaryData != null}, textInput length: ${uiState.textInput.length}")
        if (uiState.summaryData != null && !uiState.isLoading) {
            println("HomeScreen: API call completed, navigating to output")
            onNavigateToOutput()
        }
    }
    
    val filePickerUtils = FilePickerUtils(context)
    val filePicker = filePickerUtils.rememberFilePicker { uri ->
        viewModel.uploadFile(uri)
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
                    text = "Summarize AI",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Gray900
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = White
            )
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl)
                .padding(top = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            // Text Input Area
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
                BasicTextField(
                    value = uiState.textInput,
                    onValueChange = viewModel::updateTextInput,
                    textStyle = TextStyle(
                        color = Gray900,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    ),
                    cursorBrush = SolidColor(Cyan600),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.xl)
                ) { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopStart
                    ) {
                        if (uiState.textInput.isBlank()) {
                            Text(
                                text = "Paste or upload your text here...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Gray400
                            )
                        }
                        innerTextField()
                    }
                }
            }
            
            // Upload Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(CornerRadius.lg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 2.dp,
                            color = Gray300,
                            shape = RoundedCornerShape(CornerRadius.lg)
                        )
                        .padding(horizontal = Spacing.lg)
                        .clickable { filePicker() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = "Upload",
                        tint = Gray700,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = "Upload PDF or DOC",
                        style = MaterialTheme.typography.labelLarge,
                        color = Gray700
                    )
                }
            }
            
            // Error Display
            if (uiState.error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(CornerRadius.md)
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red,
                        modifier = Modifier.padding(Spacing.md)
                    )
                }
            }
            
            // Test API Button (for debugging)
            if (uiState.error != null) {
                Button(
                    onClick = {
                        // Test with a simple text to verify API connection
                        viewModel.updateTextInput("Test API connection")
                        viewModel.summarizeText()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    shape = RoundedCornerShape(CornerRadius.md)
                ) {
                    Text(
                        text = "Test API Connection",
                        style = MaterialTheme.typography.labelMedium,
                        color = White
                    )
                }
            }
            
            // Summarize Button
            Button(
                onClick = {
                    if (uiState.textInput.isNotBlank()) {
                        viewModel.summarizeText()
                    }
                },
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
                shape = RoundedCornerShape(CornerRadius.xxl),
                enabled = uiState.isSummarizeEnabled && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = "Summarizing...",
                        style = MaterialTheme.typography.labelLarge,
                        color = White,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Summarize",
                        style = MaterialTheme.typography.labelLarge,
                        color = White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

