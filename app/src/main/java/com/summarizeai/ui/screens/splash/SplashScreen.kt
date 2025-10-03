package com.summarizeai.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.summarizeai.R
import com.summarizeai.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToWelcome: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    // Animation for logo scaling
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )
    
    LaunchedEffect(Unit) {
        isVisible = true
        delay(2000) // Show splash for 2 seconds
        onNavigateToWelcome()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Cyan600,
                        Blue600
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            // App Icon with animation
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(24.dp))
                    .background(White)
                    .padding(Spacing.lg),
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Simple AI icon representation
                    Text(
                        text = "AI",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Cyan600,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // App Name
            Text(
                text = "Summarize AI",
                style = MaterialTheme.typography.headlineMedium,
                color = White,
                fontWeight = FontWeight.Bold
            )
            
            // Tagline
            Text(
                text = "Turn long text into key insights",
                style = MaterialTheme.typography.bodyLarge,
                color = White.copy(alpha = 0.9f)
            )
            
            // Loading indicator
            Spacer(modifier = Modifier.height(Spacing.xl))
            
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = White,
                strokeWidth = 3.dp
            )
        }
    }
}
