package com.example.devdeck

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.devdeck.di.appModule
import com.example.devdeck.presentation.common.Screens
import com.example.devdeck.presentation.ui.home.HomeScreen
import org.koin.compose.KoinApplication

@Composable
fun App(
    onOpenUrl: (String) -> Unit = {}
) {
    KoinApplication(application = {
        modules(appModule)
    }) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Screens.HomeScreen
        ) {
            composable<Screens.HomeScreen> {
                HomeScreen(
                    onOpenUrl = onOpenUrl
                )
            }
        }
    }
}