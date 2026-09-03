package com.example.devdeck.di

import com.example.devdeck.data.repository.PortfolioRepositoryImpl
import com.example.devdeck.domain.repository.PortfolioRepository
import com.example.devdeck.domain.usecase.GetPortfolioDataUseCase
import com.example.devdeck.presentation.ui.home.HomeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    singleOf(::PortfolioRepositoryImpl) bind PortfolioRepository::class
    factoryOf(::GetPortfolioDataUseCase)
    viewModelOf(::HomeViewModel)
}
