package com.summarizeai.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.summarizeai.presentation.viewmodel.SettingsViewModel
import com.summarizeai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var selectedLanguage by remember { mutableStateOf("English") }
    var summaryLength by remember { mutableStateOf(50f) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    
    val isStreamingEnabled by viewModel.isStreamingEnabled.collectAsStateWithLifecycle(initialValue = true)
    
    val languages = listOf("English", "Spanish", "French", "German", "Chinese")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Gray900
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = White
            )
        )
        
        // Content Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            // Language Card
            SettingsCard(
                icon = Icons.Default.Public,
                iconBackground = Cyan50,
                iconTint = Cyan600,
                title = "Language",
                description = "Choose your preferred language"
            ) {
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { }
                ) {
                    OutlinedTextField(
                        value = selectedLanguage,
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cyan600,
                            unfocusedBorderColor = Gray200,
                            focusedContainerColor = White,
                            unfocusedContainerColor = White
                        ),
                        shape = RoundedCornerShape(CornerRadius.md)
                    )
                }
            }
            
            // Summary Length Card
            SettingsCard(
                icon = Icons.Default.Description,
                iconBackground = Blue50,
                iconTint = Blue600,
                title = "Summary Length",
                description = "Adjust the default summary length"
            ) {
                Column {
                    Slider(
                        value = summaryLength,
                        onValueChange = { summaryLength = it },
                        valueRange = 10f..100f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = White,
                            activeTrackColor = Cyan600,
                            inactiveTrackColor = Gray200
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Short",
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray600
                        )
                        Text(
                            text = "${summaryLength.toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = Cyan600,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Detailed",
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray600
                        )
                    }
                }
            }
            
            // Dark Mode Card
            SettingsCard(
                icon = Icons.Default.Nightlight,
                iconBackground = Purple50,
                iconTint = Purple600,
                title = "Dark Mode",
                description = "Toggle dark theme"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Content is handled by the card
                    }
                    
                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = White,
                            checkedTrackColor = Cyan600,
                            uncheckedThumbColor = White,
                            uncheckedTrackColor = Gray200
                        )
                    )
                }
            }
            
            // Text Streaming Card
            SettingsCard(
                icon = Icons.Default.Speed,
                iconBackground = Green50,
                iconTint = Green600,
                title = "Text Streaming",
                description = "Show summaries as they're being generated"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Content is handled by the card
                    }
                    
                    Switch(
                        checked = isStreamingEnabled,
                        onCheckedChange = { viewModel.setStreamingEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = White,
                            checkedTrackColor = Cyan600,
                            uncheckedThumbColor = White,
                            uncheckedTrackColor = Gray200
                        ),
                        modifier = Modifier.testTag("streaming_toggle")
                    )
                }
            }
            
            // About Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = Elevation.sm,
                        shape = RoundedCornerShape(CornerRadius.lg),
                        ambientColor = ShadowColor,
                        spotColor = ShadowColor
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                shape = RoundedCornerShape(CornerRadius.lg)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Cyan50, Blue50)
                            )
                        )
                        .padding(Spacing.xl),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Text(
                            text = "Summarize AI",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Gray900,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Text(
                            text = "Version 1.0.0",
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray600
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        
                        Text(
                            text = "Turn long text into key insights instantly",
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray500,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBackground: androidx.compose.ui.graphics.Color,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    description: String,
    content: @Composable () -> Unit
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
                .padding(Spacing.xl),
            verticalAlignment = Alignment.Top
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(CornerRadius.md))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing.lg))
            
            // Content Area
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gray900,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500
                )
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                content()
            }
        }
    }
}
