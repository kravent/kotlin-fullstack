package me.agaman.kotlinfullstack.features

import io.ktor.application.*
import io.ktor.sessions.*
import kotlin.time.Duration.Companion.days

fun Sessions.Configuration.apiSessionsCookie() {
    cookie<ApiSession>("session", SessionStorageMemory()) {
        cookie.maxAgeInSeconds = 7.days.inWholeSeconds
    }
}

data class ApiSession(
    val csrfToken: String = CsrfTokenProvider.generateRandomToken(),
    val currentUserName: String? = null
)

fun ApplicationCall.getApiSession(): ApiSession = sessions.getOrSet { ApiSession() }
fun ApplicationCall.getCsrfToken(): String = getApiSession().csrfToken

fun ApplicationCall.setApiSession(session: ApiSession) = sessions.set(session)
fun ApplicationCall.updateApiSession(callback: (ApiSession) -> ApiSession) = sessions.set(callback(getApiSession()))
fun ApplicationCall.deleteApiSession() = sessions.clear<ApiSession>()

