package com.example.devdeck

import androidx.compose.runtime.Composable
import com.example.devdeck.di.appModule
import com.example.devdeck.presentation.ui.home.HomeScreen
import org.koin.compose.KoinApplication

@Composable
fun App(
    onOpenUrl: (String) -> Unit = {}
) {
    KoinApplication(application = {
        modules(appModule)
    }) {
        HomeScreen(
            onOpenUrl = onOpenUrl
        )
    }
}