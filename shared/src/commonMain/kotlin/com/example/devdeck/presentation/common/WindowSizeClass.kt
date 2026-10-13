package com.example.devdeck.presentation.common

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowWidthSizeClass {
    COMPACT,  // Mobile (< 600dp)
    MEDIUM,   // Foldables & Tablets (600dp - 840dp)
    EXPANDED  // Desktop & Wide Web (>= 840dp)
}

fun calculateWindowWidthSizeClass(width: Dp): WindowWidthSizeClass {
    return when {
        width < 600.dp -> WindowWidthSizeClass.COMPACT
        width < 840.dp -> WindowWidthSizeClass.MEDIUM
        else -> WindowWidthSizeClass.EXPANDED
    }
}
