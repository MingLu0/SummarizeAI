package com.summarizeai.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.summarizeai.ui.theme.*

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onLearnMore: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        // Decorative gradient circle background
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-100).dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Cyan600.copy(alpha = 0.1f),
                            Blue600.copy(alpha = 0.05f),
                            Cyan600.copy(alpha = 0.0f)
                        ),
                        radius = 400f
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl)
                .padding(vertical = Spacing.xxxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Headline
            Text(
                text = "Summarize Smarter",
                style = MaterialTheme.typography.headlineLarge,
                color = Gray900,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            // Subtitle
            Text(
                text = "Turn long text into key insights instantly",
                style = MaterialTheme.typography.bodyLarge,
                color = Gray600,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 300.dp)
            )
            
            Spacer(modifier = Modifier.height(Spacing.xxxl))
            
            // Illustration placeholder
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(Gray100),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI Illustration\nPlaceholder",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray400,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.xxxl))
            
            // Get Started Button
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Cyan600
                ),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(
                    text = "Get Started",
                    style = MaterialTheme.typography.labelLarge,
                    color = White
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            // Learn More Button
            OutlinedButton(
                onClick = onLearnMore,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Gray700
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(listOf(Gray200, Gray200))
                ),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(
                    text = "Learn More",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}

