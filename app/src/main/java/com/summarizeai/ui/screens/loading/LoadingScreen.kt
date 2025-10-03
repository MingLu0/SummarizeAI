package com.summarizeai.ui.screens.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.summarizeai.ui.theme.*

@Composable
fun LoadingScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Loading Screen",
            style = MaterialTheme.typography.headlineMedium,
            color = Gray900
        )
        
        Spacer(modifier = Modifier.height(Spacing.xl))
        
        Text(
            text = "This will show animated progress while AI processes text",
            style = MaterialTheme.typography.bodyLarge,
            color = Gray600
        )
        
        Spacer(modifier = Modifier.height(Spacing.xxl))
        
        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go Back")
        }
    }
}
