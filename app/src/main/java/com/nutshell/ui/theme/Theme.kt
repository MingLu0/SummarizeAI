package com.nutshell.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.nutshell.data.local.preferences.ThemeMode

// Spacing System
object Spacing {
    val xs = 4.dp      // 4dp
    val sm = 8.dp      // 8dp
    val md = 12.dp     // 12dp
    val lg = 16.dp     // 16dp
    val xl = 24.dp     // 24dp
    val xxl = 32.dp    // 32dp
    val xxxl = 48.dp   // 48dp
}

// Border Radius
object CornerRadius {
    val sm = 8.dp      // Small elements
    val md = 12.dp     // Medium elements
    val lg = 16.dp     // Cards
    val xl = 20.dp     // Large cards
    val xxl = 24.dp    // Buttons, major elements
    val full = 9999.dp // Circular/pill shapes
}

// Elevation/Shadows
object Elevation {
    val none = 0.dp
    val sm = 2.dp      // Subtle shadow
    val md = 4.dp      // Card shadow
    val lg = 8.dp      // Elevated elements
    val xl = 16.dp     // Modal/dialog shadow
}

// Animation Durations (in milliseconds)
object AnimationDurations {
    const val fast = 150        // Quick transitions (hover states, ripples)
    const val medium = 300      // Standard transitions (tab changes, colors)
    const val slow = 500        // Deliberate animations (modals, screens)
    const val extraSlow = 800   // Emphasis animations (success states)
}

// Animation Specifications
object AnimationSpecs {
    // Standard easing functions
    val fastEasing = FastOutSlowInEasing
    val linearEasing = LinearEasing

    // Color transitions
    fun <T> colorTransition(durationMillis: Int = AnimationDurations.medium): TweenSpec<T> =
        tween(durationMillis = durationMillis, easing = fastEasing)

    // Spring animations for bouncy effects
    val bouncySpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val gentleSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    val stiffSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh
    )

    // Fade animations
    fun fadeIn(durationMillis: Int = AnimationDurations.medium): TweenSpec<Float> =
        tween(durationMillis = durationMillis, easing = fastEasing)

    fun fadeOut(durationMillis: Int = AnimationDurations.fast): TweenSpec<Float> =
        tween(durationMillis = durationMillis, easing = fastEasing)
}

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant
)

@Composable
fun NutshellTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled for consistent branding
    content: @Composable () -> Unit
) {
    val systemInDarkTheme = isSystemInDarkTheme()
    
    val shouldUseDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDarkTheme
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (shouldUseDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        shouldUseDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use transparent status bar for edge-to-edge design
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            // Light icons for dark theme, dark icons for light theme
            windowInsetsController.isAppearanceLightStatusBars = !shouldUseDarkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !shouldUseDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NutshellTypography,
        content = content
    )
}
