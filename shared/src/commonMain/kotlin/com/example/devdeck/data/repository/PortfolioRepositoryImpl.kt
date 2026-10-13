package com.example.devdeck.data.repository

import com.example.devdeck.data.BundledPortfolioLoader
import com.example.devdeck.domain.model.PortfolioData
import com.example.devdeck.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PortfolioRepositoryImpl(
    private val loader: BundledPortfolioLoader
) : PortfolioRepository {
    override fun getPortfolioData(): Flow<PortfolioData> = flow {
        emit(loader.load())
    }
}
