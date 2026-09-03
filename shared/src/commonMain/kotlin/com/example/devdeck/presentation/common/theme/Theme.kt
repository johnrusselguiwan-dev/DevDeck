package com.example.devdeck.presentation.common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = IOSTextPrimaryDark,
    onPrimary = IOSBackgroundDark,
    primaryContainer = IOSElevatedSurfaceDark,
    onPrimaryContainer = IOSTextPrimaryDark,
    secondary = IOSTextSecondaryDark,
    onSecondary = IOSBackgroundDark,
    secondaryContainer = IOSElevatedSurfaceDark,
    onSecondaryContainer = IOSTextPrimaryDark,
    surface = IOSBackgroundDark,
    onSurface = IOSTextPrimaryDark,
    surfaceVariant = IOSSurfaceDark,
    onSurfaceVariant = IOSTextSecondaryDark,
    outline = IOSBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = IOSTextPrimaryLight,
    onPrimary = IOSSurfaceLight,
    primaryContainer = IOSElevatedSurfaceLight,
    onPrimaryContainer = IOSTextPrimaryLight,
    secondary = IOSTextSecondaryLight,
    onSecondary = IOSSurfaceLight,
    secondaryContainer = IOSElevatedSurfaceLight,
    onSecondaryContainer = IOSTextPrimaryLight,
    surface = IOSBackgroundLight,
    onSurface = IOSTextPrimaryLight,
    surfaceVariant = IOSSurfaceLight,
    onSurfaceVariant = IOSTextSecondaryLight,
    outline = IOSBorderLight
)

@Composable
fun DevDeckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
