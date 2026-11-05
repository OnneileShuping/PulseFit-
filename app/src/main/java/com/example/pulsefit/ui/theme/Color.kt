package com.example.pulsefit.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Task 1 Palette
val BgNavy = Color(0xFF0F1A21)
val SurfaceDark = Color(0xFF17252E)
val SurfaceVariantDark = Color(0xFF22343F)

val PrimaryCyan = Color(0xFF00B8F0)
val OnPrimaryDark = Color(0xFF04141B)

val SecondaryGreen = Color(0xFF28B878)
val OnSecondaryDark = Color(0xFF04140D)

val TertiaryMint = Color(0xFF7CF2C0)
val OnTertiaryDark = Color(0xFF04140D)

val PrimaryContainerDark = Color(0xFF0B3A4D)
val OnPrimaryContainerDark = Color(0xFFBFEFFF)

val OnBackgroundLight = Color(0xFFE6F2F7)
val OnSurfaceVariantLight = Color(0xFF9FB6C2)

val ErrorRed = Color(0xFFFF6B6B)
val OnErrorDark = Color(0xFF1F0000)
val ErrorContainerDark = Color(0xFF4A1212)
val OnErrorContainerDark = Color(0xFFFFDAD6)

val OutlineNavy = Color(0xFF3A5360)
val OutlineVariantNavy = Color(0xFF22343F)

// Semantic Colors
val SuccessGreen = Color(0xFF28B878)
val WarningAmber = Color(0xFFFFB347)
val DangerRed = Color(0xFFFF6B6B)

// PulseGradient: Cyan (#00B8F0) to Green (#28B878)
val PulseGradient = Brush.horizontalGradient(
    colors = listOf(PrimaryCyan, SecondaryGreen)
)
