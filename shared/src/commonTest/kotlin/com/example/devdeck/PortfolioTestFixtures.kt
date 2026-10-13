package com.example.devdeck

import com.example.devdeck.domain.model.DeveloperProfile
import com.example.devdeck.domain.model.PortfolioData

internal fun testPortfolio(): PortfolioData = PortfolioData(
    profile = DeveloperProfile(
        name = "Test Developer",
        title = "Developer",
        bio = "Builds apps",
        location = "Philippines",
        yearsExperience = "One year",
        playStoreAppsCount = 0,
        coreFocus = listOf("Kotlin")
    ),
    skills = emptyList(),
    experiences = emptyList(),
    projects = emptyList(),
    socialLinks = emptyList()
)
