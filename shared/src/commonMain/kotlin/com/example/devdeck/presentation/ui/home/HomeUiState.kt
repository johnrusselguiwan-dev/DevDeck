package com.example.devdeck.presentation.ui.home

import com.example.devdeck.domain.model.DeveloperProfile
import com.example.devdeck.domain.model.Experience
import com.example.devdeck.domain.model.Project
import com.example.devdeck.domain.model.Skill
import com.example.devdeck.domain.model.SkillCategory
import com.example.devdeck.domain.model.SocialLink
import com.example.devdeck.presentation.common.theme.ThemeMode

enum class PortfolioSection(val title: String, val index: Int) {
    ABOUT("About", 0),
    EXPERIENCE("Experience", 1),
    PROJECTS("Projects", 2),
    SKILLS("Skills", 3),
    CONTACT("Contact", 4);

    companion object {
        fun fromIndex(index: Int): PortfolioSection {
            return entries.find { it.index == index } ?: ABOUT
        }
    }
}

sealed interface HomeUiState {
    val themeMode: ThemeMode

    data class Loading(override val themeMode: ThemeMode = ThemeMode.SYSTEM) : HomeUiState
    data class Success(
        val profile: DeveloperProfile,
        val skills: List<Skill>,
        val experiences: List<Experience>,
        val projects: List<Project>,
        val socialLinks: List<SocialLink>,
        val selectedSkillCategory: SkillCategory? = null,
        override val themeMode: ThemeMode = ThemeMode.SYSTEM
    ) : HomeUiState

    data class Error(
        val message: String,
        override val themeMode: ThemeMode = ThemeMode.SYSTEM
    ) : HomeUiState
}
