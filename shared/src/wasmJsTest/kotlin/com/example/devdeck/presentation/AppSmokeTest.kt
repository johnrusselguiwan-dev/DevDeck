@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class, kotlin.js.ExperimentalWasmJsInterop::class)

package com.example.devdeck.presentation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.devdeck.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@JsFun("(theme) => window.devdeckSaveTheme(theme)")
private external fun saveBrowserTheme(theme: String)

@JsFun("() => window.localStorage.getItem('devdeck.theme')")
private external fun storedBrowserTheme(): String?

class AppSmokeTest {
    @Test
    fun appMountsWithUnrelatedGlobalKoinAndPersistsThemeThroughTheRealBrowserBridge() = runComposeUiTest {
        val unrelated = startKoin {}
        val owner = object : ViewModelStoreOwner, LifecycleOwner {
            override val viewModelStore = ViewModelStore()
            override val lifecycle = LifecycleRegistry.createUnsafe(this)
        }
        owner.lifecycle.currentState = Lifecycle.State.RESUMED
        try {
            saveBrowserTheme("DARK")
            var ready = false
            setContent {
                CompositionLocalProvider(
                    LocalViewModelStoreOwner provides owner,
                    LocalLifecycleOwner provides owner
                ) {
                    App(onReady = { ready = true })
                }
            }
            withContext(Dispatchers.Default) {
                withTimeout(10_000) {
                    while (true) {
                        awaitIdle()
                        val controls = onAllNodesWithContentDescription("Appearance: Dark Mode")
                            .fetchSemanticsNodes()
                        if (ready && controls.size == 1) break
                        delay(10)
                    }
                }
            }
            onNodeWithContentDescription("Appearance: Dark Mode").performClick()
            onNodeWithText("Light Mode").performClick()
            onNodeWithContentDescription("Appearance: Light Mode").assertExists()
            assertEquals("LIGHT", storedBrowserTheme())
            assertSame(unrelated.koin, GlobalContext.getOrNull())
            onAllNodesWithText("Contact").onFirst().performClick()
            awaitIdle()
            onNodeWithText("Share portfolio").assertExists()
        } finally {
            owner.lifecycle.currentState = Lifecycle.State.DESTROYED
            owner.viewModelStore.clear()
            saveBrowserTheme("SYSTEM")
            stopKoin()
        }
    }
}
