package com.example.devdeck.di

import com.example.devdeck.ThemePreferences
import com.example.devdeck.data.BundledPortfolioLoader
import com.example.devdeck.data.repository.PortfolioRepositoryImpl
import com.example.devdeck.domain.repository.PortfolioRepository
import com.example.devdeck.presentation.ui.home.HomeViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun appModule(themePreferences: ThemePreferences) = module {
    single { themePreferences }
    single { BundledPortfolioLoader() }
    singleOf(::PortfolioRepositoryImpl) bind PortfolioRepository::class
    viewModelOf(::HomeViewModel)
}
