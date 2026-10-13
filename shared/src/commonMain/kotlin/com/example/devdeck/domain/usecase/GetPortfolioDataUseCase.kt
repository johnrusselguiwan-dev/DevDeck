package com.example.devdeck.domain.usecase

import com.example.devdeck.domain.model.PortfolioData
import com.example.devdeck.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow

class GetPortfolioDataUseCase(
    private val repository: PortfolioRepository
) {
    operator fun invoke(): Flow<PortfolioData> = repository.getPortfolioData()
}
