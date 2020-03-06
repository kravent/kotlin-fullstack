package me.agaman.kotlinfullstack.api.utils

import io.ktor.sessions.SessionStorageMemory
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie

private const val SEVEN_DAYS_IN_SECONDS: Long = 7 * 24 * 3600

fun Sessions.Configuration.apiSessionsCookie() {
    cookie<ApiSession>("session", SessionStorageMemory()) {
        cookie.maxAgeInSeconds = SEVEN_DAYS_IN_SECONDS
    }
}

data class ApiSession(
    val currentUserName: String?
)
