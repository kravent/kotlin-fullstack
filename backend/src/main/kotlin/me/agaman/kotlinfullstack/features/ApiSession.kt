package me.agaman.kotlinfullstack.features

import io.ktor.application.ApplicationCall
import io.ktor.sessions.*

private const val SEVEN_DAYS_IN_SECONDS: Long = 7 * 24 * 3600

fun Sessions.Configuration.apiSessionsCookie() {
    cookie<ApiSession>("session", SessionStorageMemory()) {
        cookie.maxAgeInSeconds = SEVEN_DAYS_IN_SECONDS
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

