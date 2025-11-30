package com.example.markovmusic

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform