package com.example.devdeck

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform