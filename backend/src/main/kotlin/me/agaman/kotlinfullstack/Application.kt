package me.agaman.kotlinfullstack

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.sessions.Sessions
import me.agaman.kotlinfullstack.api.apiRouter
import me.agaman.kotlinfullstack.features.*
import me.agaman.kotlinfullstack.route.ApiRoute
import me.agaman.kotlinfullstack.route.Route
import me.agaman.kotlinfullstack.utils.isDevEnvironment

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        apiSerialization()
    }
    install(Sessions) {
        apiSessionsCookie()
    }
    install(Csrf) {
        validator { request ->
            request.headers["X-CSRF"].let {
                it == request.call.getCsrfToken() || (isDevEnvironment && it == "webpack-dev-server-fake-csrf-token")
            }
        }
    }
    install(Authentication) {
        form {
            skipWhen { it.getApiSession().currentUserName != null }
            challenge { call.respond(HttpStatusCode.Unauthorized) }
            validate {
                when (it.password) {
                    "testing" -> UserIdPrincipal(it.name)
                    else -> null
                }
            }
        }
    }

    install(Routing) {
        route(Route.API.path) {
            authenticate {
                post(ApiRoute.LOGIN.path) {
                    val currentUser = call.principal<UserIdPrincipal>() ?: error ("No auth found")
                    call.setApiSession(ApiSession(currentUserName = currentUser.name))
                    call.respondText { call.getCsrfToken() }
                }

                post(ApiRoute.LOGOUT.path) {
                    call.deleteApiSession()
                    call.respondText { call.getCsrfToken() }
                }

                apiRouter()
            }
        }

        static(Route.STATIC.path) {
            resources("static")
        }

        get("{...}") {
            call.respondText(ContentType.Text.Html) {
                """
                    <!doctype html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <title>Kotlin fullstack</title>
                    </head>
                    <body>
                        <input id="_csrf_token" type="hidden" value="${call.getCsrfToken()}" />
                        <div id="root"></div>
                        <script src="/static/app.js"></script>
                    </body>
                    </html>
                """.trimIndent()
            }
        }
    }
}
