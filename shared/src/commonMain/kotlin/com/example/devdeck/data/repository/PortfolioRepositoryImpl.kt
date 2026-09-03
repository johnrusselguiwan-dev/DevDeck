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
                name = "Joro Developer",
                title = "Strong Junior / Junior+ Android & KMP Engineer",
                bio = "Mobile & Cross-Platform Engineer with 6 months of startup execution. Delivered production apps to the Google Play Store, engineered KMP shared modules, and established automated CI/CD release pipelines.",
                location = "Remote / Worldwide",
                yearsExperience = "6 Months Startup Execution",
                playStoreAppsCount = 2,
                coreFocus = listOf(
                    "Native Android & Jetpack Compose",
                    "Kotlin Multiplatform (KMP/CMP)",
                    "Clean Architecture + MVVM",
                    "Google Play Console Pipelines"
                )
            )
        )
    }

    override fun getSkills(): Flow<List<Skill>> {
        return flowOf(
            listOf(
                // Architecture & System Design
                Skill(
                    id = "s1",
                    name = "Multi-Module Architecture",
                    category = SkillCategory.ARCHITECTURE,
                    level = 88,
                    description = "Structuring projects into decoupled feature and library modules for scalability."
                ),
                Skill(
                    id = "s2",
                    name = "Clean Architecture & MVVM",
                    category = SkillCategory.ARCHITECTURE,
                    level = 92,
                    description = "Strict separation of concerns into Presentation, Domain, and Data layers."
                ),
                Skill(
                    id = "s3",
                    name = "Code Reusability & DI",
                    category = SkillCategory.ARCHITECTURE,
                    level = 90,
                    description = "Leveraging Koin dependency injection and design patterns for maximum code reusability."
                ),

                // Core Mobile & Network
                Skill(
                    id = "s4",
                    name = "Native Android SDK & Idiomatic Kotlin",
                    category = SkillCategory.MOBILE_NETWORK,
                    level = 92,
                    description = "Deep knowledge of Android lifecycle, Coroutines, StateFlow, and modern Kotlin idioms."
                ),
                Skill(
                    id = "s5",
                    name = "Retrofit / Ktor API Integration",
                    category = SkillCategory.MOBILE_NETWORK,
                    level = 89,
                    description = "RESTful networking, auth interceptors, payload serialization, and robust error handling."
                ),
                Skill(
                    id = "s6",
                    name = "Web Scraping & Data Ingestion",
                    category = SkillCategory.MOBILE_NETWORK,
                    level = 85,
                    description = "Automated data parsing, HTML scraping, and structured backend/local synchronization."
                ),

                // Cross-Platform & Modern Stack
                Skill(
                    id = "s7",
                    name = "Kotlin Multiplatform (KMP)",
                    category = SkillCategory.CROSS_PLATFORM,
                    level = 88,
                    description = "Sharing business logic, repositories, and ViewModels across Android, iOS, and Web."
                ),
                Skill(
                    id = "s8",
                    name = "Compose Multiplatform (CMP)",
                    category = SkillCategory.CROSS_PLATFORM,
                    level = 90,
                    description = "Building single-codebase UI with Material 3, custom animations, and responsive web layouts."
                ),
                Skill(
                    id = "s9",
                    name = "Jetpack Compose",
                    category = SkillCategory.CROSS_PLATFORM,
                    level = 94,
                    description = "Declarative UI creation, custom layout modifiers, performance optimization, and custom themes."
                ),

                // Lifecycle & DevOps
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
                    role = "Core Mobile Contributor",
                    company = "Startup Mobile Team",
                    period = "Recent 6 Months (Production Delivery)",
                    isCurrent = true,
                    summary = "Spearheaded core mobile feature development and architecture migration for production applications targeting Android, iOS, and Web platforms.",
                    highlights = listOf(
                        "Designed and implemented multi-module KMP shared layers isolating business logic and networking.",
                        "Configured and maintained Google Play Console release tracks (Internal, Testing, Production).",
                        "Automated test build deployments using Firebase App Distribution to accelerate stakeholder feedback loops."
                    ),
                    techStack = listOf("KMP", "Compose Multiplatform", "Koin", "Ktor", "Google Play Console", "Firebase")
                ),
                Experience(
                    id = "exp2",
                    role = "Junior Android & KMP Engineer",
                    company = "Freelance & Open Source Projects",
                    period = "2025 - Present",
                    isCurrent = false,
                    summary = "Built cross-platform utility applications, open-source libraries, and interactive showcase platforms.",
                    highlights = listOf(
                        "Published full-stack KMP applications with declarative Compose UI.",
                        "Integrated REST APIs with Ktor and serialization for seamless multiplatform data flow."
                    ),
                    techStack = listOf("Jetpack Compose", "Clean Architecture", "Retrofit", "Coroutines", "StateFlow")
                )
            )
        )
    }

    override fun getProjects(): Flow<List<Project>> {
        return flowOf(
            listOf(
                Project(
                    id = "proj1",
                    title = "RewardsApp",
                    subtitle = "Checkin & Loyalty Multiplatform App",
                    description = "A production-grade Kotlin Multiplatform app featuring location-based checkins, user authentication, event registration, QR code scanning, and rewards tracking across mobile and web.",
                    techBadges = listOf("KMP", "Compose Multiplatform", "Koin", "Ktor", "Clean Architecture", "Material 3"),
                    playStoreUrl = "https://play.google.com/store/apps/details?id=com.heroapps.checkinapp",
                    githubUrl = "https://github.com/heroapps/checkin-app",
                    isFeatured = true
                ),
                Project(
                    id = "proj2",
                    title = "DevDeck",
                    subtitle = "Interactive KMP Portfolio Application",
                    description = "Cross-platform developer showcase application targeting Android, iOS, and Web (Wasm/JS). Built with Clean Architecture, MVVM, and Material 3 design system.",
                    techBadges = listOf("KMP", "Compose Multiplatform", "Jetpack Compose", "StateFlow", "Clean Architecture"),
                    playStoreUrl = null,
                    githubUrl = "https://github.com/developer/DevDeck",
                    isFeatured = true
                ),
                Project(
                    id = "proj3",
                    title = "DataIngest & Scraper",
                    subtitle = "Automated Mobile Content Pipeline",
                    description = "Android SDK application integrating web scraping, automated ingestion, background data sync, and clean presentation using Retrofit and Kotlin Coroutines.",
                    techBadges = listOf("Jetpack Compose", "Retrofit", "Jsoup", "Coroutines", "Clean Architecture"),
                    playStoreUrl = "https://play.google.com/store/apps/developer?id=DevDeck",
                    githubUrl = "https://github.com/developer/data-ingest-android",
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
                    platform = "LinkedIn",
                    url = "https://linkedin.com/in/devdeck-engineer",
                    iconIdentifier = "linkedin"
                ),
                SocialLink(
                    id = "soc2",
                    platform = "GitHub",
                    url = "https://github.com/developer",
                    iconIdentifier = "github"
                ),
                SocialLink(
                    id = "soc3",
                    platform = "Email",
                    url = "mailto:devdeck.engineer@example.com",
                    iconIdentifier = "email"
                ),
                SocialLink(
                    id = "soc4",
                    platform = "Google Play Developer Profile",
                    url = "https://play.google.com/store/apps/developer?id=DevDeck",
                    iconIdentifier = "playstore"
                )
            )
        )
    }
}
