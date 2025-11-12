package com.nutshell.ui.theme

import androidx.compose.ui.graphics.Color

// Flat Minimalist - Monochrome Base
val PureBlack = Color(0xFF000000) // Primary text, borders
val PureWhite = Color(0xFFFFFFFF) // Backgrounds, cards
val Gray900 = Color(0xFF1A1A1A) // Headings, emphasis
val Gray800 = Color(0xFF2D2D2D) // Body text
val Gray700 = Color(0xFF4A4A4A) // Secondary text
val Gray600 = Color(0xFF6B7280) // Icons (active), borders
val Gray500 = Color(0xFF9CA3AF) // Placeholder text
val Gray400 = Color(0xFF9CA3AF) // Disabled text
val Gray300 = Color(0xFFD1D5DB) // Borders (light)
val Gray200 = Color(0xFFE5E7EB) // Dividers
val Gray100 = Color(0xFFF5F5F5) // Subtle backgrounds
val Gray50 = Color(0xFFFAFAFA) // Off-white background

// Bold Accent Colors - Royal Purple (Bold & Creative)
val RoyalPurple = Color(0xFF8B5CF6) // Primary CTA - Sophisticated & innovative
val RoyalPurpleDark = Color(0xFF7C3AED) // CTA hover/pressed (deeper purple)
val RoyalPurpleLight = Color(0xFFA78BFA) // CTA disabled (lighter purple)

// Legacy names for backward compatibility
val ElectricLime = RoyalPurple
val ElectricLimeDark = RoyalPurpleDark
val ElectricLimeLight = RoyalPurpleLight
val SoftMint = RoyalPurple
val SoftMintDark = RoyalPurpleDark
val SoftMintLight = RoyalPurpleLight

// Secondary Accent (for variety)
val HotPink = Color(0xFFFF006E) // Secondary actions
val HotPinkDark = Color(0xFFE6006B) // Secondary hover

// Semantic Colors (Flat)
val SuccessGreen = Color(0xFF10B981) // Success states
val ErrorRed = Color(0xFFEF4444) // Error states
val WarningOrange = Color(0xFFF59E0B) // Warning states

// Legacy color mappings (for backward compatibility during transition)
val Cyan600 = RoyalPurple
val Blue600 = HotPink
val Cyan700 = ElectricLimeDark
val Blue700 = HotPinkDark
val White = PureWhite
val Cyan50 = Gray50
val Blue50 = Gray50
val Purple50 = Gray100
val Purple600 = HotPink
val Green50 = Gray50
val Green600 = SuccessGreen
val Red50 = Gray100
val Red600 = ErrorRed
val ShadowColor = Color(0x00000000) // Transparent (no shadows in flat design)
val AccentShadow = Color(0x00000000) // Transparent (no shadows in flat design)

// Light Theme Colors
val LightPrimary = Cyan600
val LightOnPrimary = White
val LightPrimaryContainer = Cyan50
val LightOnPrimaryContainer = Cyan700

val LightSecondary = Blue600
val LightOnSecondary = White
val LightSecondaryContainer = Blue50
val LightOnSecondaryContainer = Blue700

val LightTertiary = Purple600
val LightOnTertiary = White
val LightTertiaryContainer = Purple50
val LightOnTertiaryContainer = Purple600

val LightError = Red600
val LightOnError = White
val LightErrorContainer = Red50
val LightOnErrorContainer = Red600

val LightBackground = Gray50
val LightOnBackground = Gray900
val LightSurface = White
val LightOnSurface = Gray900
val LightSurfaceVariant = Gray100
val LightOnSurfaceVariant = Gray700

val LightOutline = Gray200
val LightOutlineVariant = Gray300

// Dark Theme Colors - True Black with Royal Purple Accent
val DarkPrimary = RoyalPurple // Royal Purple - prominent in dark mode
val DarkOnPrimary = PureBlack // Black text on purple buttons
val DarkPrimaryContainer = RoyalPurpleDark // Darker purple for containers
val DarkOnPrimaryContainer = Gray100 // Light text on purple containers

val DarkSecondary = HotPink // Hot pink secondary
val DarkOnSecondary = PureBlack // Black text on pink
val DarkSecondaryContainer = HotPinkDark // Darker pink
val DarkOnSecondaryContainer = Gray100 // Light text on pink containers

val DarkTertiary = RoyalPurpleLight // Lighter purple tertiary
val DarkOnTertiary = PureBlack // Black text on light purple
val DarkTertiaryContainer = RoyalPurple // Standard purple
val DarkOnTertiaryContainer = Gray100 // Light text

val DarkError = ErrorRed // Red for errors
val DarkOnError = PureWhite // White text on red
val DarkErrorContainer = Color(0xFF5F1A1A) // Dark red container
val DarkOnErrorContainer = Color(0xFFFFB4AB) // Light red text

val DarkBackground = PureBlack // True black background (OLED-friendly)
val DarkOnBackground = Gray100 // Light gray text on black
val DarkSurface = Color(0xFF121212) // Slightly elevated black for cards
val DarkOnSurface = Gray100 // Light gray text on surfaces
val DarkSurfaceVariant = Gray900 // Dark gray for variants
val DarkOnSurfaceVariant = Gray300 // Medium gray text

val DarkOutline = Gray700 // Gray borders
val DarkOutlineVariant = Gray800 // Darker borders
