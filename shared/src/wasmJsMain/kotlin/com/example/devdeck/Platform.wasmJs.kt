@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.example.devdeck

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override val isWeb: Boolean = true
}

actual fun getPlatform(): Platform = WasmPlatform()

@JsFun("() => window.devdeckLoadTheme()")
private external fun loadTheme(): String

@JsFun("(value) => window.devdeckSaveTheme(value)")
private external fun saveTheme(value: String)

@JsFun("(url, callback) => window.devdeckShare(url, callback)")
private external fun sharePortfolio(url: String, callback: (String) -> Unit)

@Composable
actual fun rememberThemePreferences(): ThemePreferences = remember {
    ThemePreferences(read = ::loadTheme, write = ::saveTheme)
}

@Composable
actual fun rememberPortfolioShareLauncher(onResult: (ShareResult) -> Unit): (String) -> Unit {
    val report by rememberUpdatedState(onResult)
    return { url ->
        sharePortfolio(url) { result -> report(ShareResult.valueOf(result)) }
    }
}
