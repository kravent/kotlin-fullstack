package me.agaman.kotlinfullstack.features

import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.ApplicationRequest
import io.ktor.request.httpMethod
import io.ktor.response.respond
import io.ktor.util.AttributeKey
import java.security.SecureRandom
import java.util.*

typealias CsrfValidator = (ApplicationRequest) -> Boolean

class Csrf(config: Configuration) {
    private val validators: List<CsrfValidator> = config.validators.toList()

    class Configuration {
        internal var validators: MutableList<CsrfValidator> = mutableListOf()

        fun validator(validator: CsrfValidator) {
            this.validators.add(validator)
        }

        fun validateHeader(headerName: String, calculateExpectedContent: (ApplicationRequest) -> String?) {
            validator { request ->
                calculateExpectedContent(request)
                    ?.let { request.headers[headerName] == it }
                    ?: true
            }
        }
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, Csrf> {
        override val key = AttributeKey<Csrf>("Csrf")

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): Csrf {
            val csrf = Csrf(Configuration().apply(configure))
            pipeline.intercept(ApplicationCallPipeline.Features) {
                if (call.request.httpMethod != HttpMethod.Get) {
                    if (csrf.validators.any { !it(call.request) }) {
                        call.respond(HttpStatusCode.Forbidden)
                        finish()
                    }
                }
            }
            return csrf
        }
    }
}

object CsrfTokenProvider {
    private val secureRandom = SecureRandom()

    fun generateRandomToken(): String {
        val bytes = ByteArray(256)
        secureRandom.nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }
}
