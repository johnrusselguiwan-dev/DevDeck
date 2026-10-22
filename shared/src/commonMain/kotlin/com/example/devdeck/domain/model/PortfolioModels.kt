package com.example.devdeck.domain.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class DeveloperProfile(
    val name: String,
    val title: String,
    val bio: String,
    val location: String,
    val yearsExperience: String,
    val playStoreAppsCount: Int,
    val coreFocus: List<String>,
    val resumeUrl: String? = null,
    val androidDownloadUrl: String? = null,
    val websiteUrl: String? = null
)

@Serializable
enum class SkillCategory(val displayName: String) {
    MOBILE_DEV("Android & Mobile Dev"),
    CROSS_PLATFORM("Multiplatform & Web"),
    ARCHITECTURE("Architecture & Design"),
    DEVOPS("DevOps & Play Console")
}

enum class PortfolioResource {
    RESUME,
    ANDROID_APP,
    WEBSITE
}

@Serializable
data class Skill(
    val id: String,
    val name: String,
    val category: SkillCategory,
    @Required
    val specialty: String = "Core Expertise",
    val description: String
)

@Serializable
data class Experience(
    val id: String,
    val role: String,
    val company: String,
    val period: String,
    val isCurrent: Boolean,
    val summary: String,
    val highlights: List<String>,
    val techStack: List<String>,
    val isEducation: Boolean = false
)

@Serializable
data class Project(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val techBadges: List<String>,
    val playStoreUrl: String? = null,
    val githubUrl: String? = null,
    @Required
    val isFeatured: Boolean = true,
    val contributions: List<String> = emptyList()
)

@Serializable
data class SocialLink(
    val id: String,
    val platform: String,
    val url: String,
    val iconIdentifier: String
)
