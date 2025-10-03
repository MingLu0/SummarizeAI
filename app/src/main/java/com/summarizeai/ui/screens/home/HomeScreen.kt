package com.summarizeai.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.summarizeai.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToLoading: () -> Unit,
    onNavigateToOutput: () -> Unit
) {
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
                .padding(Spacing.xl),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Home Screen",
                style = MaterialTheme.typography.headlineMedium,
                color = Gray900
            )
            
            Spacer(modifier = Modifier.height(Spacing.xl))
            
            Text(
                text = "This will be the main input screen with text area and upload functionality",
                style = MaterialTheme.typography.bodyLarge,
                color = Gray600
            )
            
            Spacer(modifier = Modifier.height(Spacing.xxl))
            
            Button(
                onClick = onNavigateToLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Loading Screen")
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            Button(
                onClick = onNavigateToOutput,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Output Screen")
            }
        }
    }
}
