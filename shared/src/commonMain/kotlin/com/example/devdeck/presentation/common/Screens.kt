package com.example.devdeck.presentation.common

import kotlinx.serialization.Serializable

sealed interface Screens {
    @Serializable
    object HomeScreen : Screens

    @Serializable
    data class ProjectDetailScreen(
        val projectId: String,
        val projectTitle: String
    ) : Screens
}
