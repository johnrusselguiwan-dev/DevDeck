package com.example.devdeck.domain.usecase

import com.example.devdeck.domain.model.DeveloperProfile
import com.example.devdeck.domain.model.Experience
import com.example.devdeck.domain.model.Project
import com.example.devdeck.domain.model.Skill
import com.example.devdeck.domain.model.SocialLink
import com.example.devdeck.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class PortfolioData(
    val profile: DeveloperProfile,
    val skills: List<Skill>,
    val experiences: List<Experience>,
    val projects: List<Project>,
    val socialLinks: List<SocialLink>
)

class GetPortfolioDataUseCase(
    private val repository: PortfolioRepository
) {
    operator fun invoke(): Flow<PortfolioData> {
        return combine(
            repository.getProfile(),
            repository.getSkills(),
            repository.getExperiences(),
            repository.getProjects(),
            repository.getSocialLinks()
        ) { profile, skills, experiences, projects, socialLinks ->
            PortfolioData(
                profile = profile,
                skills = skills,
                experiences = experiences,
                projects = projects,
                socialLinks = socialLinks
            )
        }
    }
}
