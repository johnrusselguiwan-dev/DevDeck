package com.example.devdeck.presentation.ui.home

import kotlin.test.Test
import kotlin.test.assertEquals

class SectionVisibilityTest {
    @Test
    fun aboutIsSelectedWhenAtTopEvenIfExperienceIsTaller() {
        assertEquals(
            PortfolioSection.ABOUT,
            activePortfolioSection(
                items = listOf(SectionBounds(0, 0, 300), SectionBounds(1, 300, 1500)),
                viewportStart = 0,
                viewportEnd = 800,
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
                atEnd = true
            )
        )
    }

    @Test
    fun unmeasuredListStartsAtAbout() {
        assertEquals(PortfolioSection.ABOUT, activePortfolioSection(emptyList(), 0, 0, atEnd = true))
    }
}
