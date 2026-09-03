package com.example.devdeck.presentation.ui.home

import com.example.devdeck.domain.model.DeveloperProfile
import com.example.devdeck.domain.model.Experience
import com.example.devdeck.domain.model.Project
import com.example.devdeck.domain.model.Skill
import com.example.devdeck.domain.model.SkillCategory
import com.example.devdeck.domain.model.SocialLink

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val profile: DeveloperProfile,
        val skills: List<Skill>,
        val experiences: List<Experience>,
        val projects: List<Project>,
        val socialLinks: List<SocialLink>,
        val selectedSkillCategory: SkillCategory? = null,
        val isDarkMode: Boolean = true
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
