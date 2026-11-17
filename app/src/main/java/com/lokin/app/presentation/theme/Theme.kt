package com.lokin.app.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary              = PrimaryBlue,
    onPrimary            = CardLight,
    primaryContainer     = Color(0xFFE1EFFE),
    onPrimaryContainer   = PrimaryBlueDark,
    secondary            = PrimaryGreen,
    onSecondary          = CardLight,
    secondaryContainer   = Color(0xFFDEF7EC),
    onSecondaryContainer = Color(0xFF03543F),
    tertiary             = AccentOrange,
    tertiaryContainer   = Color(0xFFFFEDD5),
    onTertiaryContainer = Color(0xFF7C2D12),
    background           = SurfaceLight,
    onBackground         = OnSurfaceLight,
    surface              = CardLight,
    onSurface            = OnSurfaceLight,
    surfaceVariant       = Color(0xFFE5E7EB),
    onSurfaceVariant     = SubTextLight,
    error                = AccentRed,
    errorContainer       = Color(0xFFFDE8E8),
    onErrorContainer     = Color(0xFF9B1C1C),
)

private val DarkColorScheme = darkColorScheme(
    primary              = Color(0xFF93BFFF),
    onPrimary            = PrimaryBlueDark,
    primaryContainer     = PrimaryBlueDark,
    onPrimaryContainer   = Color(0xFFE1EFFE),
    secondary            = PrimaryGreen,
    onSecondary          = CardDark,
    secondaryContainer   = Color(0xFF014737),
    onSecondaryContainer = Color(0xFFDEF7EC),
    tertiary             = AccentOrange,
    tertiaryContainer   = Color(0xFF7C2D12),
    onTertiaryContainer = Color(0xFFFFEDD5),
    background           = SurfaceDark,
    onBackground         = OnSurfaceDark,
    surface              = CardDark,
    onSurface            = OnSurfaceDark,
    surfaceVariant       = Color(0xFF374151),
    onSurfaceVariant     = SubTextDark,
    error                = Color(0xFFFC8181),
    errorContainer       = Color(0xFF771D1D),
    onErrorContainer     = Color(0xFFFDE8E8),
)

@Composable
fun LokInTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = LokInTypography,
        content     = content
    )
}