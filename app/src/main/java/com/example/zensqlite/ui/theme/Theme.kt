package com.example.zensqlite.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ZenColorScheme = lightColorScheme(
    primary = DarkNavy,
    onPrimary = Color.White,
    primaryContainer = DeepBlue,
    onPrimaryContainer = Color.White,
    secondary = RoyalBlue,
    onSecondary = Color.White,
    secondaryContainer = RoyalBlue.copy(alpha = 0.1f),
    onSecondaryContainer = RoyalBlue,
    tertiary = CoralRed,
    onTertiary = Color.White,
    tertiaryContainer = CoralRed.copy(alpha = 0.1f),
    onTertiaryContainer = CoralRed,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed,
    background = LightBackground,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary,
    outline = TextTertiary,
    outlineVariant = Color(0xFFE5E7EB)
)

@Composable
fun ZenSQLiteTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkNavy.toArgb()
            window.navigationBarColor = DarkNavy.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = ZenColorScheme,
        typography = AppTypography,
        content = content
    )
}