package me.agaman.kotlinfullstack.features

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import java.security.SecureRandom
import java.util.*

class Csrf(val config: Config) {
    @KtorDsl
    class Config {
        var ignoredMethods: Set<HttpMethod> = setOf(HttpMethod.Get)
        var headerName: String = "X-CSRF"
        var csrfProvider: (ApplicationCall) -> String? = { null }
    }

    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Config, Csrf> {
        override val key: AttributeKey<Csrf> = AttributeKey("Csrf")

        override fun install(pipeline: ApplicationCallPipeline, configure: Config.() -> Unit): Csrf {
            val plugin = Csrf(Config().also(configure))

            pipeline.intercept(ApplicationCallPipeline.Plugins) {
                if (!plugin.config.ignoredMethods.contains(context.request.httpMethod)) {
                    val expectedCsrf = plugin.config.csrfProvider(call)
                    if (context.request.header(plugin.config.headerName) != expectedCsrf) {
                        context.respond(HttpStatusCode.Forbidden)
                        finish()
                    }
                }
            }

            return plugin
        }
    }
}
