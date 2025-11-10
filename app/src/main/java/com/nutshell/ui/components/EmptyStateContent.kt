package com.nutshell.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nutshell.ui.theme.Gray100
import com.nutshell.ui.theme.Gray400
import kotlinx.coroutines.launch

/**
 * Reusable empty state component with entrance animation
 *
 * Features:
 * - Circular icon background
 * - Bold title
 * - Descriptive subtitle
 * - Smooth scale and fade entrance animation
 *
 * @param icon The icon to display
 * @param iconTint The tint color for the icon (default: Gray400)
 * @param iconBackground The background color for the icon container (default: Gray100)
 * @param title The main title text
 * @param description The description text
 * @param modifier Optional modifier for the container
 */
@Composable
fun EmptyStateContent(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    iconTint: Color = Gray400,
    iconBackground: Color = Gray100
) {
    // Entrance animations
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            )
        }
    }

    Column(
        modifier = modifier
            .scale(scale.value),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon Container
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    color = iconBackground.copy(alpha = alpha.value),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint.copy(alpha = alpha.value),
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha.value),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha.value),
            textAlign = TextAlign.Center
        )
    }
}
