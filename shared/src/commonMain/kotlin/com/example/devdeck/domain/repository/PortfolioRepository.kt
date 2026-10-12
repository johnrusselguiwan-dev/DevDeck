package com.example.devdeck.domain.repository

import com.example.devdeck.domain.model.PortfolioData
import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    fun getPortfolioData(): Flow<PortfolioData>
}
