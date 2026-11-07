package com.example.devdeck

import androidx.compose.runtime.Composable
import com.example.devdeck.presentation.common.theme.ThemeMode

class ThemePreferences(
    private val read: () -> String? = { null },
    private val write: (String) -> Unit = {}
) {
    fun load(): ThemeMode = runCatching { read() }.getOrNull()
        .let { saved -> ThemeMode.entries.firstOrNull { it.name == saved } ?: ThemeMode.SYSTEM }

    fun save(mode: ThemeMode) {
        runCatching { write(mode.name) }
    }
}

@Composable
expect fun rememberThemePreferences(): ThemePreferences
