package com.example.devdeck

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.example.devdeck.di.appModule
import com.example.devdeck.domain.model.PortfolioResource
import com.example.devdeck.presentation.ui.home.HomeScreen
import org.koin.compose.KoinIsolatedContext
import org.koin.dsl.koinApplication

@Composable
fun App(
    onOpenUrl: (String) -> Unit = {},
    onResourceClick: (PortfolioResource) -> Unit = {},
    themePreferences: ThemePreferences = rememberThemePreferences(),
    onReady: () -> Unit = {}
) {
    val application = remember(themePreferences) {
        koinApplication { modules(appModule(themePreferences)) }
    }
    DisposableEffect(application) {
        onDispose { application.close() }
    }
    KoinIsolatedContext(context = application) {
        HomeScreen(
            onOpenUrl = onOpenUrl,
            onResourceClick = onResourceClick
        )
        LaunchedEffect(Unit) { onReady() }
    }
}
