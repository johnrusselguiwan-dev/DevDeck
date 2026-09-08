package com.example.devdeck.presentation.common

sealed interface Screens {
    object HomeScreen : Screens

    data class ProjectDetailScreen(
        val projectId: String,
        val projectTitle: String
    ) : Screens
}
