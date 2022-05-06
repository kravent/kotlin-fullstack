package me.agaman.kotlinfullstack

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.*
import me.agaman.kotlinfullstack.api.apiRouter
import me.agaman.kotlinfullstack.features.*
import me.agaman.kotlinfullstack.route.Route

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
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
            post(Route.LOGIN.path) {
                val currentUser = call.principal<UserIdPrincipal>() ?: error("No auth found")
                call.setApiSession(ApiSession(currentUserName = currentUser.name))
                call.respondText { call.getCsrfToken() }
            }

            post(Route.LOGOUT.path) {
                call.deleteApiSession()
                call.respondText { call.getCsrfToken() }
            }

            route(Route.API.path) {
                apiRouter()
            }
        }

        get("/favicon.ico") {
            call.respond(HttpStatusCode.NotFound)
        }
        static(Route.STATIC.path) {
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
