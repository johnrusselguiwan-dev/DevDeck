package com.example.devdeck.presentation.ui.home

import kotlin.test.Test
import kotlin.test.assertEquals

class SectionVisibilityTest {
    @Test
    fun shortAboutRemainsSelectedAtTheTopOfAWideViewport() {
        assertEquals(
            PortfolioSection.ABOUT,
            activePortfolioSection(
                items = listOf(SectionBounds(0, 12, 148), SectionBounds(1, 208, 1500)),
                viewportStart = 0,
                viewportEnd = 960,
                atStart = true,
                atEnd = false
            )
        )
    }

    @Test
    fun aboutIsSelectedWhenAtTopEvenIfExperienceIsTaller() {
        assertEquals(
            PortfolioSection.ABOUT,
            activePortfolioSection(
                items = listOf(SectionBounds(0, 0, 300), SectionBounds(1, 300, 1500)),
                viewportStart = 0,
                viewportEnd = 800,
                atStart = true,
                atEnd = false
            )
        )
    }

    @Test
    fun tallSectionWinsWhenItsCenterIsOutsideTheViewport() {
        assertEquals(
            PortfolioSection.PROJECTS,
            activePortfolioSection(
                items = listOf(SectionBounds(1, -200, 300), SectionBounds(2, 120, 2400)),
                viewportStart = 0,
                viewportEnd = 700,
                atStart = false,
                atEnd = false
            )
        )
    }

    @Test
    fun countsOnlyTheVisiblePortionOfEachSection() {
        assertEquals(
            PortfolioSection.SKILLS,
            activePortfolioSection(
                items = listOf(SectionBounds(2, -2000, 2100), SectionBounds(3, 120, 500)),
                viewportStart = 0,
                viewportEnd = 600,
                atStart = false,
                atEnd = false
            )
        )
    }

    @Test
    fun reachingTheBottomSelectsContactEvenWhenSkillsOccupiesMoreSpace() {
        assertEquals(
            PortfolioSection.CONTACT,
            activePortfolioSection(
                items = listOf(SectionBounds(3, -100, 650), SectionBounds(4, 570, 130)),
                viewportStart = 0,
                viewportEnd = 700,
                atStart = false,
                atEnd = true
            )
        )
    }

    @Test
    fun unmeasuredListStartsAtAbout() {
        assertEquals(PortfolioSection.ABOUT, activePortfolioSection(emptyList(), 0, 0, atStart = true, atEnd = true))
    }

    @Test
    fun scrollingAwayFromTheTopAllowsExperienceToBecomeActive() {
        assertEquals(
            PortfolioSection.EXPERIENCE,
            activePortfolioSection(
                items = listOf(SectionBounds(0, -8, 148), SectionBounds(1, 188, 1500)),
                viewportStart = 0,
                viewportEnd = 960,
                atStart = false,
                atEnd = false
            )
        )
    }

    @Test
    fun aPageThatFitsTheViewportKeepsAboutSelected() {
        assertEquals(
            PortfolioSection.ABOUT,
            activePortfolioSection(
                items = listOf(SectionBounds(0, 12, 148), SectionBounds(4, 208, 100)),
                viewportStart = 0,
                viewportEnd = 960,
                atStart = true,
                atEnd = true
            )
        )
    }
}
