package com.summarizeai.ui.screens.output

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.summarizeai.ui.theme.*
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutputScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        // Top App Bar with Back Button
        TopAppBar(
            title = {
                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Gray900
                )
            },
            navigationIcon = {
                TextButton(onClick = onNavigateBack) {
                    Text("← Back")
                }
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
                text = "Output Screen",
                style = MaterialTheme.typography.headlineMedium,
                color = Gray900
            )
            
            Spacer(modifier = Modifier.height(Spacing.xl))
            
            Text(
                text = "This will display the AI-generated summary with different length options and action buttons",
                style = MaterialTheme.typography.bodyLarge,
                color = Gray600
            )
        }
    }
}
