package me.agaman.kotlinfullstack

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.*
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.resources.Resources
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.sessions.Sessions
import kotlinx.html.*
import me.agaman.kotlinfullstack.api.apiRouter
import me.agaman.kotlinfullstack.features.*
import me.agaman.kotlinfullstack.route.Api

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Resources)
    install(ContentNegotiation) {
        json()
    }
    install(Compression) {
        gzip()
    }
    install(Sessions) {
        apiSessionsCookie()
    }
    install(Csrf) {
        validateHeader("X-CSRF") { it.call.getCsrfToken() }
    }

    authentication {
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

    routing {
        authenticate {
            post<Api.Login> {
                val currentUser = call.principal<UserIdPrincipal>() ?: error("No auth found")
                call.setApiSession(ApiSession(currentUserName = currentUser.name))
                call.respondText { call.getCsrfToken() }
            }

            post<Api.Logout> {
                call.deleteApiSession()
                call.respondText { call.getCsrfToken() }
            }

            apiRouter()
        }

        get("/favicon.ico") {
            call.respond(HttpStatusCode.NotFound)
        }
        static("/static") {
            resources("static")
        }

        get("{...}") {
            val csrfToken = call.getCsrfToken()
            call.respondHtml {
                head {
                    meta { charset = Charsets.UTF_8.name() }
                    link {
                        rel = LinkRel.stylesheet
                        href = "https://fonts.googleapis.com/css?family=Roboto:300,400,500"
                    }
                    link {
                        rel = LinkRel.stylesheet
                        href = "https://fonts.googleapis.com/icon?family=Material+Icons"
                    }
                    title { +"Kotlin fullstack" }
                }
                body {
                    hiddenInput {
                        id = "_csrf_token"
                        value = csrfToken
                    }
                    div { id = "root" }
                    script {
                        type = ScriptType.textJavaScript
                        src = "/static/app.js"
                    }
                }
            }
        }
    }
}
