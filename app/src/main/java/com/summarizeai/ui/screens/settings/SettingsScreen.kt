package com.summarizeai.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Globe
import androidx.compose.material.icons.filled.Moon
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.summarizeai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var selectedLanguage by remember { mutableStateOf("English") }
    var summaryLength by remember { mutableStateOf(50f) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    
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
                icon = Icons.Default.Globe,
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
                icon = Icons.Default.TextFields,
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
                icon = Icons.Default.Moon,
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