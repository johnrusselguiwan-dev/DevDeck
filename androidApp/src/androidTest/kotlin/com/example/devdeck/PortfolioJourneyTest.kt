package com.example.devdeck

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PortfolioJourneyTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun themeAndContactNavigationSurviveActivityRecreation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val preferences = context.getSharedPreferences("devdeck", Context.MODE_PRIVATE)
        val previous = preferences.getString("theme", null)
        try {
            compose.waitUntil(10_000) {
                compose.onAllNodesWithContentDescription("Appearance:", substring = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithContentDescription("Appearance:", substring = true).performClick()
            compose.onNodeWithText("Dark Mode").performClick()
            compose.onAllNodesWithText("Contact").onFirst().performClick()
            waitForContact()
            compose.activityRule.scenario.recreate()
            waitForContact()
            compose.onNodeWithContentDescription("Appearance: Dark Mode").assertIsDisplayed()
            compose.onNodeWithText("Share portfolio").assertIsDisplayed()
        } finally {
            preferences.edit().apply {
                if (previous == null) remove("theme") else putString("theme", previous)
            }.commit()
        }
    }

    private fun waitForContact() {
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Share portfolio").fetchSemanticsNodes().isNotEmpty()
        }
    }
}
