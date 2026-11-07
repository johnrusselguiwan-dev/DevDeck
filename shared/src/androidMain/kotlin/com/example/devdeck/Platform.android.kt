package com.example.devdeck

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import com.example.devdeck.shared.generated.resources.Res
import com.example.devdeck.shared.generated.resources.share_portfolio
import org.jetbrains.compose.resources.stringResource

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val isWeb: Boolean = false
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun rememberThemePreferences(): ThemePreferences {
    val context = LocalContext.current.applicationContext
    return remember(context) {
        val preferences = context.getSharedPreferences("devdeck", Context.MODE_PRIVATE)
        ThemePreferences(
            read = { preferences.getString("theme", null) },
            write = { preferences.edit { putString("theme", it) } }
        )
    }
}

@Composable
actual fun rememberPortfolioShareLauncher(onResult: (ShareResult) -> Unit): (String) -> Unit {
    val context = LocalContext.current
    val report by rememberUpdatedState(onResult)
    val chooserTitle = stringResource(Res.string.share_portfolio)
    return { url ->
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, url)
            }
            context.startActivity(Intent.createChooser(intent, chooserTitle))
            report(ShareResult.SHARED)
        } catch (_: ActivityNotFoundException) {
            report(ShareResult.FAILED)
        } catch (_: SecurityException) {
            report(ShareResult.FAILED)
        }
    }
}
