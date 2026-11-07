@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.example.devdeck

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.events.Event

@JsFun(
    "() => typeof window.devdeckLoadTheme === 'function' && " +
        "typeof window.devdeckSaveTheme === 'function' && typeof window.devdeckShare === 'function'"
)
private external fun platformHelpersReady(): Boolean

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    try {
        check(platformHelpersReady()) { "Portfolio platform helpers did not load" }
        val container = requireNotNull(document.getElementById("portfolio-app")) { "Missing portfolio app container" }
        ComposeViewport(container, configure = { isA11YEnabled = true }) {
            App(
                onReady = {
                    document.getElementById("bootstrap-status")?.let { it.parentNode?.removeChild(it) }
                    window.dispatchEvent(Event("devdeck:ready"))
                },
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
        window.dispatchEvent(Event("devdeck:failed"))
    }
}
