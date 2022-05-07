package ajax.features

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.http.HttpMethod
import io.ktor.util.AttributeKey
import io.ktor.util.KtorDsl

class Csrf private constructor(val config: Config) {
    @KtorDsl
    class Config {
        var ignoredMethods: Set<HttpMethod> = setOf(HttpMethod.Get)
        var headerName: String = "X-CSRF"
        var csrfProvider: () -> String? = { null }
    }

    companion object Plugin : HttpClientPlugin<Config, Csrf> {
        override val key: AttributeKey<Csrf> = AttributeKey("Csrf")

        override fun prepare(block: Config.() -> Unit): Csrf = Csrf(Config().also(block))

        override fun install(plugin: Csrf, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                if (!plugin.config.ignoredMethods.contains(context.method)) {
                    plugin.config.csrfProvider()?.let {
                        context.header(plugin.config.headerName, it)
                    }
                }
            }
        }
    }
}
