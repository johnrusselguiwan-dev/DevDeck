package com.example.devdeck.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DeveloperProfile(
    val name: String = "Joro Developer",
    val title: String = "Strong Junior / Junior+ Android & KMP Engineer",
    val bio: String = "Mobile & Cross-Platform Engineer with 6 months of intense startup execution. Proven track record of delivering production apps to the Google Play Store, building KMP shared modules, and architecting scalable Android solutions.",
    val location: String = "Remote / Worldwide",
    val yearsExperience: String = "6+ Months Startup Execution",
    val playStoreAppsCount: Int = 2,
    val coreFocus: List<String> = listOf(
        "Android SDK & Jetpack Compose",
        "Kotlin Multiplatform (KMP/CMP)",
        "Clean Architecture & MVVM",
        "Play Console Release Pipelines"
    )
)

enum class SkillCategory(val displayName: String) {
    ARCHITECTURE("Architecture & System Design"),
    MOBILE_NETWORK("Core Mobile & Network"),
    CROSS_PLATFORM("Cross-Platform & Modern Stack"),
    DEVOPS("Lifecycle & DevOps")
}

@Serializable
data class Skill(
    val id: String,
    val name: String,
    val category: SkillCategory,
    val level: Int, // 1 to 100
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
    val techStack: List<String>
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
    val isFeatured: Boolean = true
)

@Serializable
data class SocialLink(
    val id: String,
    val platform: String,
    val url: String,
    val iconIdentifier: String
)
