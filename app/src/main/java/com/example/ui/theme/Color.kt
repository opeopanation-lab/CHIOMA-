package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Chioma Brand Palette (African-Futurist Terminal)
val DarkBackground = Color(0xFF0F0F1A)
val DarkSurface = Color(0xFF16162A)
val DarkSurfaceElevated = Color(0xFF22223B)
val ChiomaPurple = Color(0xFF7C3AED)
val ChiomaDeepPurple = Color(0xFF4A148C)
val ElectricCyan = Color(0xFF00E5FF)
val ElectricCyanGlow = Color(0x3300E5FF)
val TextPrimary = Color(0xFFE2E2E6)
val TextSecondary = Color(0xFF9CA3AF)
val UserBubbleColor = Color(0xFF262640)
val ChiomaBubbleColor = Color(0x1F7C3AED)
val SuccessGreen = Color(0xFF10B981)
val ErrorRed = Color(0xFFEF4444)

// Frosted Glassmorphism custom backdrop-filter and glass transparency theme variables
// rgba(26, 26, 46, 0.6) yields Color(26, 26, 46, 153) (where alpha 153 / 255 = ~0.60)
object LiquidGlass {
    val backgroundColor = Color(26, 26, 46, 153)       // rgba(26, 26, 46, 0.6)
    val borderColor = Color(255, 255, 255, 26)          // rgba(255, 255, 255, 0.1)
    val accentColor = Color(255, 255, 255, 51)          // rgba(255, 255, 255, 0.2)
    const val borderWidthDp = 1
    const val borderRadiusDp = 16
}

val GlassmorphismSurfaceColor = LiquidGlass.backgroundColor
val GlassmorphismBorderColor = LiquidGlass.borderColor
val GlassmorphismAccentColor = LiquidGlass.accentColor


