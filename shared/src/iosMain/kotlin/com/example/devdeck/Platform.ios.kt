package com.example.devdeck

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val isWeb: Boolean = false
}

actual fun getPlatform(): Platform = IOSPlatform()

@Composable
actual fun rememberThemePreferences(): ThemePreferences = remember {
    val preferences = NSUserDefaults.standardUserDefaults
    ThemePreferences(
        read = { preferences.stringForKey("devdeck.theme") },
        write = { preferences.setObject(it, forKey = "devdeck.theme") }
    )
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberPortfolioShareLauncher(onResult: (ShareResult) -> Unit): (String) -> Unit {
    val report by rememberUpdatedState(onResult)
    return { url ->
        var presenter = UIApplication.sharedApplication.keyWindow?.rootViewController
        while (presenter?.presentedViewController != null) {
            presenter = presenter.presentedViewController
        }
        val controller = presenter
        if (controller == null) {
            report(ShareResult.FAILED)
        } else {
            val sheet = UIActivityViewController(activityItems = listOf(url), applicationActivities = null)
            sheet.completionWithItemsHandler = { _, completed, _, error ->
                report(
                    when {
                        error != null -> ShareResult.FAILED
                        completed -> ShareResult.SHARED
                        else -> ShareResult.CANCELLED
                    }
                )
            }
            sheet.popoverPresentationController?.let { popover ->
                popover.sourceView = controller.view
                popover.sourceRect = controller.view.bounds
                popover.permittedArrowDirections = 0uL
            }
            controller.presentViewController(sheet, animated = true, completion = null)
        }
    }
}
