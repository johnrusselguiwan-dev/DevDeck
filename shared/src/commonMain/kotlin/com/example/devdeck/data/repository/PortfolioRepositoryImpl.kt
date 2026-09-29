package com.example.devdeck.data.repository

import com.example.devdeck.domain.model.DeveloperProfile
import com.example.devdeck.domain.model.Experience
import com.example.devdeck.domain.model.Project
import com.example.devdeck.domain.model.Skill
import com.example.devdeck.domain.model.SkillCategory
import com.example.devdeck.domain.model.SocialLink
import com.example.devdeck.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class PortfolioRepositoryImpl : PortfolioRepository {

    override fun getProfile(): Flow<DeveloperProfile> {
        return flowOf(
            DeveloperProfile(
                name = "John Russel Guiwan",
                title = "Android & Jetpack Compose Engineer",
                bio = "Android Developer skilled in Java, Native Kotlin, Jetpack Compose, and Multi-Module Architecture. Currently practicing Kotlin Multiplatform (KMP/CMP) to explore cross-platform development — developing this DevDeck app as a hands-on learning project to test KMP/CMP capabilities.",
                location = "Philippines",
                yearsExperience = "Android & Multi-Module Developer",
                playStoreAppsCount = 1,
                coreFocus = listOf(
                    "Java & Native Android SDK",
                    "Idiomatic Kotlin & Coroutines",
                    "Jetpack Compose UI",
                    "Multi-Module Clean Architecture",
                    "REST APIs & Web Ingestion",
                    "KMP / CMP (Practice & Exploring)"
                )
            )
        )
    }

    override fun getSkills(): Flow<List<Skill>> {
        return flowOf(
            listOf(
                Skill(
                    id = "s1",
                    name = "Java & Android SDK",
                    category = SkillCategory.MOBILE_NETWORK,
                    level = 92,
                    description = "Core Java OOP principles, Android lifecycle, Services, BroadcastReceivers, and native Android development."
                ),
                Skill(
                    id = "s2",
                    name = "Idiomatic Kotlin & Coroutines",
                    category = SkillCategory.MOBILE_NETWORK,
                    level = 94,
                    description = "Kotlin coroutines, StateFlow/SharedFlow, extension functions, sealed interfaces, and modern idiomatic code."
                ),
                Skill(
                    id = "s3",
                    name = "Jetpack Compose",
                    category = SkillCategory.CROSS_PLATFORM,
                    level = 95,
                    description = "Declarative UI creation, state management, custom modifiers, Material 3 design, and performance optimizations."
                ),
                Skill(
                    id = "s4",
                    name = "Kotlin Multiplatform (KMP / CMP)",
                    category = SkillCategory.CROSS_PLATFORM,
                    level = 68,
                    description = "Practicing and exploring cross-platform development. Built this DevDeck app as a hands-on learning project to test shared Kotlin logic and Compose UI across platforms."
                ),
                Skill(
                    id = "s5",
                    name = "Multi-Module Architecture",
                    category = SkillCategory.ARCHITECTURE,
                    level = 90,
                    description = "Structuring projects into decoupled feature modules and core infrastructure libraries for scalability and build speed."
                ),
                Skill(
                    id = "s6",
                    name = "Clean Architecture & MVVM",
                    category = SkillCategory.ARCHITECTURE,
                    level = 92,
                    description = "Strict separation of concerns into Presentation, Domain, and Data layers with unidirectional data flow."
                ),
                Skill(
                    id = "s7",
                    name = "Dependency Injection (Koin / Hilt)",
                    category = SkillCategory.ARCHITECTURE,
                    level = 88,
                    description = "Injecting ViewModels, repositories, and network services using Koin and Hilt."
                ),
                Skill(
                    id = "s8",
                    name = "Retrofit / Ktor API Integration",
                    category = SkillCategory.MOBILE_NETWORK,
                    level = 89,
                    description = "RESTful networking, JSON serialization, auth interceptors, and robust error handling."
                ),
                Skill(
                    id = "s9",
                    name = "Web Scraping & Data Ingestion",
                    category = SkillCategory.MOBILE_NETWORK,
                    level = 85,
                    description = "Automated web scraping, HTML parsing with Jsoup, and local offline-first persistence."
                ),
                Skill(
                    id = "s10",
                    name = "Google Play Console Release Management",
                    category = SkillCategory.DEVOPS,
                    level = 87,
                    description = "Managing Internal, Closed Testing, and Production release tracks, app sign keys, and store listings."
                ),
                Skill(
                    id = "s11",
                    name = "Firebase App Distribution",
                    category = SkillCategory.DEVOPS,
                    level = 88,
                    description = "Beta build delivery, tester group management, and automated build artifact distribution."
                ),
                Skill(
                    id = "s12",
                    name = "Agile SDLC & Release Pipelines",
                    category = SkillCategory.DEVOPS,
                    level = 86,
                    description = "Iterative sprint execution, continuous integration, versioning strategy, and rapid deployment."
                )
            )
        )
    }

    override fun getExperiences(): Flow<List<Experience>> {
        return flowOf(
            listOf(
                Experience(
                    id = "exp1",
                    role = "Android & Multi-Module Developer",
                    company = "Android Development",
                    period = "Present",
                    isCurrent = true,
                    summary = "Developing production-ready native Android applications utilizing Java, Kotlin, Jetpack Compose, and multi-module architecture.",
                    highlights = listOf(
                        "Engineered modular Android architectures isolating UI components, data repositories, and feature modules.",
                        "Integrated REST APIs, Coroutines, StateFlow, and Jetpack Compose for modern, reactive user experiences.",
                        "Practicing Kotlin Multiplatform (KMP) to expand cross-platform knowledge and build cross-platform showcase apps."
                    ),
                    techStack = listOf("Java", "Kotlin", "Jetpack Compose", "Multi-Module", "Koin", "Retrofit", "KMP Practice")
                )
            )
        )
    }

    override fun getProjects(): Flow<List<Project>> {
        return flowOf(
            listOf(
                Project(
                    id = "proj1",
                    title = "DevDeck",
                    subtitle = "Interactive KMP Practice Portfolio App",
                    description = "Developer portfolio application created as hands-on practice to explore Kotlin Multiplatform and Compose Multiplatform across Android and platforms. Built with Clean Architecture, MVVM, and Material 3 design.",
                    techBadges = listOf("Jetpack Compose", "KMP Practice", "Compose Multiplatform", "Clean Architecture", "Material 3"),
                    playStoreUrl = null,
                    githubUrl = "https://github.com/johnrusselguiwan-dev",
                    isFeatured = true
                ),
                Project(
                    id = "proj2",
                    title = "Multi-Module Android App",
                    subtitle = "Scalable Android Architecture",
                    description = "Production-grade Android application structured into clean, decoupled feature modules using Jetpack Compose, MVVM, and Retrofit.",
                    techBadges = listOf("Java", "Kotlin", "Jetpack Compose", "Multi-Module", "MVVM"),
                    playStoreUrl = null,
                    githubUrl = "https://github.com/johnrusselguiwan-dev",
                    isFeatured = true
                )
            )
        )
    }

    override fun getSocialLinks(): Flow<List<SocialLink>> {
        return flowOf(
            listOf(
                SocialLink(
                    id = "soc1",
                    platform = "GitHub",
                    url = "https://github.com/johnrusselguiwan-dev",
                    iconIdentifier = "github"
                ),
                SocialLink(
                    id = "soc2",
                    platform = "Facebook",
                    url = "https://www.facebook.com/russel.guiwan/",
                    iconIdentifier = "facebook"
                ),
                SocialLink(
                    id = "soc3",
                    platform = "Email",
                    url = "mailto:russelguiwan@gmail.com",
                    iconIdentifier = "email"
                )
            )
        )
    }
}
