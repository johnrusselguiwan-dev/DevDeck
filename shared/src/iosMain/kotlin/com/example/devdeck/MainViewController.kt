package com.example.devdeck

import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSLog
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@OptIn(ExperimentalForeignApi::class)
fun MainViewController() = ComposeUIViewController {
    App(
        onOpenUrl = { urlString ->
            val url = NSURL.URLWithString(urlString)
            if (url != null) {
                UIApplication.sharedApplication.openURL(
                    url = url,
                    options = emptyMap<Any?, Any>(),
                    completionHandler = { opened ->
                        if (!opened) NSLog("Unable to open portfolio link")
                    }
                )
            } else {
                NSLog("Invalid portfolio link")
            }
        }
    )
}
