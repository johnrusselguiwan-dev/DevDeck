package com.example.devdeck.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devdeck.domain.model.SkillCategory
import com.example.devdeck.domain.usecase.GetPortfolioDataUseCase
import com.example.devdeck.presentation.common.theme.ThemeMode
import kotlinx.coroutines.Job
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

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var loadingJob: Job? = null
    private var selectedSkillCategory: SkillCategory? = null

    init {
        loadPortfolioData()
    }

    fun loadPortfolioData() {
        loadingJob?.cancel()
        loadingJob = viewModelScope.launch {
            getPortfolioDataUseCase()
                .onStart {
                    _uiState.update { current ->
                        if (current is HomeUiState.Success) current else HomeUiState.Loading(current.themeMode)
                    }
                }
                .catch { e ->
                    _uiState.update { current ->
                        HomeUiState.Error(e.message ?: "Failed to load portfolio data", current.themeMode)
                    }
                }
                .collect { data ->
                    _uiState.update { current ->
                        HomeUiState.Success(
                            profile = data.profile,
                            skills = data.skills,
                            experiences = data.experiences,
                            projects = data.projects,
                            socialLinks = data.socialLinks,
                            selectedSkillCategory = selectedSkillCategory,
                            themeMode = current.themeMode
                        )
                    }
                }
        }
    }

    fun filterSkillsByCategory(category: SkillCategory?) {
        selectedSkillCategory = category
        _uiState.update { currentState ->
            if (currentState is HomeUiState.Success) {
                currentState.copy(selectedSkillCategory = category)
            } else {
                currentState
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _uiState.update { currentState ->
            when (currentState) {
                is HomeUiState.Success -> currentState.copy(themeMode = mode)
                is HomeUiState.Loading -> currentState.copy(themeMode = mode)
                is HomeUiState.Error -> currentState.copy(themeMode = mode)
            }
        }
    }
}
