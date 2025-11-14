package com.nutshell.ui.screens.home

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutshell.presentation.viewmodel.HomeUiState
import com.nutshell.ui.theme.*
import com.nutshell.utils.FilePickerUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    webContentError: String? = null,
    onUpdateTextInput: (String) -> Unit,
    onSummarizeText: () -> Unit,
    onUploadFile: (Uri) -> Unit,
    onNavigateToStreaming: (String) -> Unit = {},
    onNavigateToOutput: () -> Unit = {},
    onClearNavigationFlags: () -> Unit = {},
) {
    val context = LocalContext.current

    // Handle navigation based on UI state
    LaunchedEffect(uiState.shouldNavigateToStreaming, uiState.shouldNavigateToOutput) {
        if (uiState.shouldNavigateToStreaming) {
            onNavigateToStreaming(uiState.textInput)
            // Clear the navigation flag to prevent auto-navigation on return
            onClearNavigationFlags()
        } else if (uiState.shouldNavigateToOutput) {
            onNavigateToOutput()
            // Clear the navigation flag to prevent auto-navigation on return
            onClearNavigationFlags()
        }
    }

    val filePickerUtils = FilePickerUtils(context)
    val filePicker = filePickerUtils.rememberFilePicker { uri ->
        onUploadFile(uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Text Input Area - Flat with Border and Animated Focus
            var isTextFieldFocused by remember { mutableStateOf(false) }

            val borderColor by animateColorAsState(
                targetValue = if (isTextFieldFocused) {
                    MaterialTheme.colorScheme.primary
                } else {
                    if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else PureBlack
                },
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                label = "textFieldBorderColor",
            )

            val borderWidth by animateDpAsState(
                targetValue = if (isTextFieldFocused) 3.dp else 2.dp,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                label = "textFieldBorderWidth",
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(
                        width = borderWidth,
                        color = borderColor,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                BasicTextField(
                    value = uiState.textInput,
                    onValueChange = onUpdateTextInput,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .onFocusChanged { focusState ->
                            isTextFieldFocused = focusState.isFocused
                        },
                ) { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopStart,
                    ) {
                        if (uiState.textInput.isBlank()) {
                            Text(
                                text = "Paste or upload your text here...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            )
                        }
                        innerTextField()
                    }
                }
            }

            // Upload Button - Flat Outlined
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        width = 2.dp,
                        color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else PureBlack,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { filePicker() }
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = "Upload",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Upload PDF or DOC",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            // Error Display - Flat with Slide-In Animation
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { -it / 2 },
                    animationSpec = tween(250),
                ) + fadeOut(animationSpec = tween(250)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.error,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            // Web Content Extraction Error Display - Flat with Slide-In Animation
            AnimatedVisibility(
                visible = webContentError != null,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { -it / 2 },
                    animationSpec = tween(250),
                ) + fadeOut(animationSpec = tween(250)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = WarningOrange,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                ) {
                    Column {
                        Text(
                            text = "Failed to extract web content",
                            style = MaterialTheme.typography.labelLarge,
                            color = WarningOrange,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = webContentError ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarningOrange,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "💡 Tip: You can copy the article text and paste it in the text area above to summarize it manually.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Test API Button (for debugging) - Flat
            if (uiState.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(MaterialTheme.colorScheme.error)
                        .clickable {
                            onUpdateTextInput("Test API connection")
                            onSummarizeText()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Test API connection",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = MaterialTheme.colorScheme.onError,
                    )
                }
            }

            // Summarize Button - Primary CTA (Flat) with Scale Animation
            val buttonInteractionSource = remember { MutableInteractionSource() }
            val isButtonPressed by buttonInteractionSource.collectIsPressedAsState()

            val buttonScale by animateFloatAsState(
                targetValue = if (isButtonPressed && uiState.isSummarizeEnabled && !uiState.isLoading) 0.97f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh,
                ),
                label = "buttonScale",
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .scale(buttonScale)
                    .background(
                        color = if (uiState.isSummarizeEnabled && !uiState.isLoading) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        },
                        shape = RoundedCornerShape(12.dp),
                    )
                    .clickable(
                        enabled = uiState.isSummarizeEnabled && !uiState.isLoading,
                        interactionSource = buttonInteractionSource,
                        indication = rememberRipple(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                    ) {
                        if (uiState.textInput.isNotBlank()) {
                            onSummarizeText()
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (uiState.isLoading) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Summarizing...",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                            ),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                } else {
                    Text(
                        text = "Summarize →",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp,
                        ),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenFlatMinimalistPreview() {
    NutshellTheme {
        HomeScreen(
            uiState = HomeUiState(
                textInput = "",
                isLoading = false,
                isSummarizeEnabled = true,
                error = null,
                shouldNavigateToOutput = false,
                shouldNavigateToStreaming = false,
            ),
            webContentError = null,
            onUpdateTextInput = {},
            onSummarizeText = {},
            onUploadFile = {},
            onNavigateToStreaming = {},
            onNavigateToOutput = {},
            onClearNavigationFlags = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenWithContentPreview() {
    NutshellTheme {
        HomeScreen(
            uiState = HomeUiState(
                textInput = "This is a sample text that demonstrates how the new flat minimalist design looks with actual content in the text input field.",
                isLoading = false,
                isSummarizeEnabled = true,
                error = null,
                shouldNavigateToOutput = false,
                shouldNavigateToStreaming = false,
            ),
            webContentError = null,
            onUpdateTextInput = {},
            onSummarizeText = {},
            onUploadFile = {},
            onNavigateToStreaming = {},
            onNavigateToOutput = {},
            onClearNavigationFlags = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenLoadingPreview() {
    NutshellTheme {
        HomeScreen(
            uiState = HomeUiState(
                textInput = "Sample text being summarized...",
                isLoading = true,
                isSummarizeEnabled = false,
                error = null,
                shouldNavigateToOutput = false,
                shouldNavigateToStreaming = false,
            ),
            webContentError = null,
            onUpdateTextInput = {},
            onSummarizeText = {},
            onUploadFile = {},
            onNavigateToStreaming = {},
            onNavigateToOutput = {},
            onClearNavigationFlags = {},
        )
    }
}
