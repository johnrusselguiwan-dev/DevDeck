package com.example.devdeck

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.devdeck.di.appModule
import com.example.devdeck.presentation.ui.home.HomeScreen
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

@Composable
fun App(
    onOpenUrl: (String) -> Unit = {}
) {
    remember {
        GlobalContext.getOrNull() ?: startKoin {
            modules(appModule)
        }
    }

    HomeScreen(
        onOpenUrl = onOpenUrl
    )
}