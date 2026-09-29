package com.example.devdeck.presentation.ui.home

import com.example.devdeck.domain.model.DeveloperProfile
import com.example.devdeck.domain.model.Experience
import com.example.devdeck.domain.model.Project
import com.example.devdeck.domain.model.Skill
import com.example.devdeck.domain.model.SkillCategory
import com.example.devdeck.domain.model.SocialLink
import com.example.devdeck.presentation.common.theme.ThemeMode

enum class NavigationTab(val title: String) {
    OVERVIEW("Overview"),
    PROJECTS("Projects"),
    SKILLS("Skills"),
    EXPERIENCE("Experience")
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val profile: DeveloperProfile,
        val skills: List<Skill>,
        val experiences: List<Experience>,
        val projects: List<Project>,
        val socialLinks: List<SocialLink>,
        val selectedSkillCategory: SkillCategory? = null,
        val selectedTab: NavigationTab = NavigationTab.OVERVIEW,
        val themeMode: ThemeMode = ThemeMode.SYSTEM
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
