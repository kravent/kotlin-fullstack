package me.agaman.kotlinfullstack

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.sessions.*
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
