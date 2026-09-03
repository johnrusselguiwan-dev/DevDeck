package com.example.devdeck.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devdeck.domain.model.SkillCategory
import com.example.devdeck.domain.usecase.GetPortfolioDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getPortfolioDataUseCase: GetPortfolioDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadPortfolioData()
    }

    fun loadPortfolioData() {
        viewModelScope.launch {
            getPortfolioDataUseCase()
                .onStart { _uiState.value = HomeUiState.Loading }
                .catch { e -> _uiState.value = HomeUiState.Error(e.message ?: "Failed to load portfolio data") }
                .collect { data ->
                    _uiState.value = HomeUiState.Success(
                        profile = data.profile,
                        skills = data.skills,
                        experiences = data.experiences,
                        projects = data.projects,
                        socialLinks = data.socialLinks
                    )
                }
        }
    }

    fun filterSkillsByCategory(category: SkillCategory?) {
        _uiState.update { currentState ->
            if (currentState is HomeUiState.Success) {
                currentState.copy(selectedSkillCategory = category)
            } else {
                currentState
            }
        }
    }

    fun toggleDarkMode() {
        _uiState.update { currentState ->
            if (currentState is HomeUiState.Success) {
                currentState.copy(isDarkMode = !currentState.isDarkMode)
            } else {
                currentState
            }
        }
    }
}
