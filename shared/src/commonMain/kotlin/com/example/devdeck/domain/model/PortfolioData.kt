package com.example.devdeck.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioData(
    val profile: DeveloperProfile,
    val skills: List<Skill>,
    val experiences: List<Experience>,
    val projects: List<Project>,
    val socialLinks: List<SocialLink>
)
