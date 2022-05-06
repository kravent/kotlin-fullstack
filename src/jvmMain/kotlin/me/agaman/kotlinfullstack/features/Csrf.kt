package me.agaman.kotlinfullstack.features

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import java.security.SecureRandom
import java.util.*

object CsrfTokenProvider {
    private val secureRandom = SecureRandom()

    fun generateRandomToken(): String =
        ByteArray(256)
            .also { secureRandom.nextBytes(it) }
            .let { Base64.getEncoder().encodeToString(it) }
}

val Csrf: RouteScopedPlugin<CsrfConfig> = createRouteScopedPlugin("Csrf", ::CsrfConfig) {
    val validators = pluginConfig.getValidators()

    on(PluginInterception) {
        if (call.request.httpMethod != HttpMethod.Get) {
            if (validators.any { !it(call.request) }) {
                call.respond(HttpStatusCode.Forbidden)
                finish()
            }
        }
    }
}

typealias CsrfValidator = (ApplicationRequest) -> Boolean

@KtorDsl
class CsrfConfig {
    private var validators: MutableList<CsrfValidator> = mutableListOf()

    fun getValidators() = validators.toList()

    fun validator(validator: CsrfValidator) {
        this.validators.add(validator)
    }

    fun validateHeader(headerName: String, calculateExpectedContent: (ApplicationRequest) -> String?) {
        validator { request ->
            calculateExpectedContent(request)
                ?.let { request.header(headerName) == it }
                ?: true
        }
    }
}

private object PluginInterception : Hook<suspend PipelineContext<Unit, ApplicationCall>.() -> Unit> {
    override fun install(
        pipeline: ApplicationCallPipeline,
        handler: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit,
    ) {
        pipeline.intercept(ApplicationCallPipeline.Plugins) {
            handler()
        }
    }
}
