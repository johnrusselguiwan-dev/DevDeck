package com.example.devdeck

interface Platform {
    val name: String
    val isWeb: Boolean
}

expect fun getPlatform(): Platform
