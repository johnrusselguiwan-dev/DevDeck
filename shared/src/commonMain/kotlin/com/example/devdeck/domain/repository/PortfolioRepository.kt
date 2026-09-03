package com.example.devdeck.domain.repository

import com.example.devdeck.domain.model.DeveloperProfile
import com.example.devdeck.domain.model.Experience
import com.example.devdeck.domain.model.Project
import com.example.devdeck.domain.model.Skill
import com.example.devdeck.domain.model.SocialLink
import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    fun getProfile(): Flow<DeveloperProfile>
    fun getSkills(): Flow<List<Skill>>
    fun getExperiences(): Flow<List<Experience>>
    fun getProjects(): Flow<List<Project>>
    fun getSocialLinks(): Flow<List<SocialLink>>
}
