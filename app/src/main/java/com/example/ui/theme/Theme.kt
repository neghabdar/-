package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private val ParsmindaLightScheme = lightColorScheme(
    primary = PersianTurquoise,
    onPrimary = CreamText,
    primaryContainer = DeepPersianBlue,
    onPrimaryContainer = WarmGoldLight,
    secondary = SaffronGold,
    onSecondary = InkBrown,
    secondaryContainer = ParchmentDark,
    onSecondaryContainer = InkBrown,
    tertiary = RoyalViolet,
    onTertiary = CreamText,
    background = DeepPersianBlue,
    onBackground = CreamText,
    surface = ParchmentCream,
    onSurface = InkBrown,
    surfaceVariant = ParchmentDark,
    onSurfaceVariant = MutedInk,
    outline = ParchmentBorder,
    error = RubyCrimson,
    onError = Color.White
)

private val ParsmindaDarkScheme = darkColorScheme(
    primary = PersianTurquoiseLight,
    onPrimary = DeepPersianBlue,
    primaryContainer = LapisBlue,
    onPrimaryContainer = WarmGoldLight,
    secondary = SaffronGold,
    onSecondary = InkBrown,
    secondaryContainer = Color(0xFF3E2D1D),
    onSecondaryContainer = ParchmentCream,
    tertiary = Color(0xFF9F75FF),
    onTertiary = Color.White,
    background = MidnightIndigo,
    onBackground = CreamText,
    surface = Color(0xFF142D4C),
    onSurface = CreamText,
    surfaceVariant = Color(0xFF1B3B61),
    onSurfaceVariant = Color(0xFFD7E4F0),
    outline = AntiqueBrass,
    error = Color(0xFFFF6B6B),
    onError = Color.Black
)

val ParsmindaShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun ParsmindaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ParsmindaDarkScheme else ParsmindaLightScheme
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ParsmindaTypography,
            shapes = ParsmindaShapes,
            content = content
        )
    }
}
