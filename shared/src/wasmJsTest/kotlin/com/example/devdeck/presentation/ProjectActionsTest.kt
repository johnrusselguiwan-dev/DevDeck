@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.example.devdeck.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.devdeck.domain.model.Project
import com.example.devdeck.presentation.common.theme.DevDeckTheme
import com.example.devdeck.presentation.ui.components.ProjectsShowcase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProjectActionsTest {
    @Test
    fun narrowCardsKeepLargeActionLabelsReadableAndOpenTheirOwnUrls() = runComposeUiTest {
        val opened = mutableListOf<String>()
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, 2f)) {
                DevDeckTheme {
                    Box(Modifier.width(320.dp).verticalScroll(rememberScrollState())) {
                        ProjectsShowcase(
                            projects = listOf(
                                Project(
                                    id = "test",
                                    title = "App",
                                    subtitle = "Android",
                                    description = "Project",
                                    techBadges = emptyList(),
                                    playStoreUrl = "https://example.com/store",
                                    githubUrl = "https://example.com/source"
                                )
                            ),
                            onOpenUrl = { opened.add(it) }
                        )
                    }
                }
            }
        }
        listOf("Play Store", "View source").forEach { label ->
            val layouts = mutableListOf<TextLayoutResult>()
            onNodeWithText(label, useUnmergedTree = true)
                .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            assertTrue(layouts.isNotEmpty())
            val layout = layouts.single()
            assertTrue(layout.multiParagraph.height <= layout.size.height + 1f, "$label height")
            assertFalse(layout.multiParagraph.didExceedMaxLines, label)
            repeat(layout.lineCount) { line ->
                assertTrue(layout.getLineLeft(line) >= -1f, "$label left edge")
                assertTrue(layout.getLineRight(line) <= layout.size.width + 1f, "$label right edge")
                assertFalse(layout.isLineEllipsized(line), label)
            }
            onNodeWithText(label).performScrollTo().performClick()
        }
        assertEquals(listOf("https://example.com/store", "https://example.com/source"), opened)
    }
}
