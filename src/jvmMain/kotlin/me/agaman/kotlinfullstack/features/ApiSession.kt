package me.agaman.kotlinfullstack.features

import io.ktor.server.application.ApplicationCall
import io.ktor.server.sessions.*
import java.security.SecureRandom
import java.util.*
import kotlin.time.Duration.Companion.days

fun SessionsConfig.apiSessionsCookie() {
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

private object CsrfTokenProvider {
    private val secureRandom = SecureRandom()

    fun generateRandomToken(): String =
        ByteArray(256)
            .also { secureRandom.nextBytes(it) }
            .let { Base64.getEncoder().encodeToString(it) }
}
