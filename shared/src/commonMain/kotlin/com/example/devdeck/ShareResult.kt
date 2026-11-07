package com.example.devdeck

import androidx.compose.runtime.Composable

enum class ShareResult {
    SHARED,
    COPIED,
    CANCELLED,
    FAILED
}

@Composable
expect fun rememberPortfolioShareLauncher(onResult: (ShareResult) -> Unit): (String) -> Unit
