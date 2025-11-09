package com.nutshell.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutshell.R
import com.nutshell.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit
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
        onNavigateToMain()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            // App Icon with animation - matching app's flat minimalist style
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .border(
                        width = 2.dp,
                        color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else PureBlack,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(Spacing.lg),
                contentAlignment = Alignment.Center
            ) {
                // Simple AI icon representation
                Text(
                    text = "AI",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // App Name
            Text(
                text = "Nutshell",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            // Tagline
            Text(
                text = "Turn long text into key insights",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            // Loading indicator
            Spacer(modifier = Modifier.height(Spacing.xl))

            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        }
    }
}

