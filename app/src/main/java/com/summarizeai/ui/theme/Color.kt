package com.summarizeai.ui.theme

import androidx.compose.ui.graphics.Color

// Flat Minimalist - Monochrome Base
val PureBlack = Color(0xFF000000)    // Primary text, borders
val PureWhite = Color(0xFFFFFFFF)    // Backgrounds, cards
val Gray900 = Color(0xFF1A1A1A)      // Headings, emphasis
val Gray800 = Color(0xFF2D2D2D)      // Body text
val Gray700 = Color(0xFF4A4A4A)      // Secondary text
val Gray600 = Color(0xFF6B7280)      // Icons (active), borders
val Gray500 = Color(0xFF9CA3AF)      // Placeholder text
val Gray400 = Color(0xFF9CA3AF)      // Disabled text
val Gray300 = Color(0xFFD1D5DB)      // Borders (light)
val Gray200 = Color(0xFFE5E7EB)      // Dividers
val Gray100 = Color(0xFFF5F5F5)      // Subtle backgrounds
val Gray50 = Color(0xFFFAFAFA)       // Off-white background

// Bold Accent Colors - Royal Purple (Bold & Creative)
val RoyalPurple = Color(0xFF8B5CF6)       // Primary CTA - Sophisticated & innovative
val RoyalPurpleDark = Color(0xFF7C3AED)   // CTA hover/pressed (deeper purple)
val RoyalPurpleLight = Color(0xFFA78BFA)  // CTA disabled (lighter purple)

// Legacy names for backward compatibility
val ElectricLime = RoyalPurple
val ElectricLimeDark = RoyalPurpleDark
val ElectricLimeLight = RoyalPurpleLight
val SoftMint = RoyalPurple
val SoftMintDark = RoyalPurpleDark
val SoftMintLight = RoyalPurpleLight

// Secondary Accent (for variety)
val HotPink = Color(0xFFFF006E)           // Secondary actions
val HotPinkDark = Color(0xFFE6006B)       // Secondary hover

// Semantic Colors (Flat)
val SuccessGreen = Color(0xFF10B981)      // Success states
val ErrorRed = Color(0xFFEF4444)          // Error states
val WarningOrange = Color(0xFFF59E0B)     // Warning states

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
val ShadowColor = Color(0x00000000)       // Transparent (no shadows in flat design)
val AccentShadow = Color(0x00000000)      // Transparent (no shadows in flat design)

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

// Future: Dark theme colors (for future enhancement)
val DarkPrimary = Cyan600
val DarkOnPrimary = Gray900
val DarkPrimaryContainer = Cyan700
val DarkOnPrimaryContainer = Cyan50

val DarkSecondary = Blue600
val DarkOnSecondary = Gray900
val DarkSecondaryContainer = Blue700
val DarkOnSecondaryContainer = Blue50

val DarkTertiary = Purple600
val DarkOnTertiary = Gray900
val DarkTertiaryContainer = Purple600
val DarkOnTertiaryContainer = Purple50

val DarkError = Red600
val DarkOnError = Gray900
val DarkErrorContainer = Red600
val DarkOnErrorContainer = Red50

val DarkBackground = Gray900
val DarkOnBackground = Gray50
val DarkSurface = Gray800
val DarkOnSurface = Gray50
val DarkSurfaceVariant = Gray700
val DarkOnSurfaceVariant = Gray300

val DarkOutline = Gray600
val DarkOutlineVariant = Gray700
