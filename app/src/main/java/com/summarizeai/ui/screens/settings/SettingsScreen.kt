package com.summarizeai.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.summarizeai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isStreamingEnabled: Boolean,
    onSetStreamingEnabled: (Boolean) -> Unit
) {
    var selectedLanguage by remember { mutableStateOf("English") }
    var summaryLength by remember { mutableStateOf(50f) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    
    
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
                text = "SETTINGS",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp
                ),
                color = PureBlack,
                modifier = Modifier.padding(bottom = 8.dp)
            )
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
                            thumbColor = ElectricLime,
                            activeTrackColor = ElectricLime,
                            inactiveTrackColor = Gray300
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
                            color = PureBlack,
                            fontWeight = FontWeight.Bold
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
                            checkedThumbColor = PureBlack,
                            checkedTrackColor = ElectricLime,
                            uncheckedThumbColor = Gray400,
                            uncheckedTrackColor = Gray300
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
                        onCheckedChange = onSetStreamingEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureBlack,
                            checkedTrackColor = ElectricLime,
                            uncheckedThumbColor = Gray400,
                            uncheckedTrackColor = Gray300
                        ),
                        modifier = Modifier.testTag("streaming_toggle")
                    )
                }
            }
            
            // About Card - Flat with Electric Lime Accent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = ElectricLime,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(PureWhite)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SUMMARIZE AI",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = PureBlack
                    )
                    
                    Text(
                        text = "Version 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray700,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Turn long text into key insights instantly",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray700,
                        textAlign = TextAlign.Center
                    )
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
    // Flat Card with Border
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Gray300,
                shape = RoundedCornerShape(12.dp)
            )
            .background(PureWhite)
            .padding(20.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon Container
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = ElectricLime.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PureBlack,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Content Area
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = PureBlack
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Gray700
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            content()
        }
    }
}
