package com.example.devdeck

import androidx.compose.ui.window.ComposeUIViewController
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

fun MainViewController() = ComposeUIViewController {
    App(
        onOpenUrl = { urlString ->
            val url = NSURL.URLWithString(urlString)
            if (url != null) {
                UIApplication.sharedApplication.openURL(url)
            }
        }
    )
}