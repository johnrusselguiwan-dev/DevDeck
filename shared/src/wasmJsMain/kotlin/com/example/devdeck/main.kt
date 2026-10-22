package com.example.devdeck

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.events.Event

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    try {
        document.getElementById("spinner")?.let { it.parentNode?.removeChild(it) }

        val container = requireNotNull(document.getElementById("portfolio-app")) { "Missing portfolio app container" }
        ComposeViewport(container, configure = { isA11YEnabled = true }) {
            App(
                onResourceClick = { resource ->
                    window.dispatchEvent(Event("devdeck:${resource.name.lowercase()}"))
                },
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
