package com.programmersbox.kmmtest

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform