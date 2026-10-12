package com.example.devdeck

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    try {
        document.getElementById("spinner")?.let { it.parentNode?.removeChild(it) }

        val body = document.body ?: return
        ComposeViewport(body, configure = { isA11YEnabled = true }) {
            App(
                onOpenUrl = { url ->
                    if (url.startsWith("mailto:") || url.startsWith("tel:")) {
                        window.location.href = url
                    } else {
                        window.open(url, "_blank", "noopener,noreferrer")
                    }
                }
            )
        }
    } catch (e: Throwable) {
        println("WASM Exception during main: ${e.message}")
        e.printStackTrace()
    }
}
