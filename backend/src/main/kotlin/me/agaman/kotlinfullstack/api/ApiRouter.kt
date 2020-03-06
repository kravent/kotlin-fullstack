package me.agaman.kotlinfullstack.api

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.sessions.*
import me.agaman.kotlinfullstack.api.utils.ApiSession
import me.agaman.kotlinfullstack.api.utils.apiSerialization
import me.agaman.kotlinfullstack.api.utils.apiSessionsCookie
import me.agaman.kotlinfullstack.model.UserCreateRequest
import me.agaman.kotlinfullstack.model.UserCreateResponse
import me.agaman.kotlinfullstack.model.UserListResponse
import me.agaman.kotlinfullstack.route.ApiRoute

private val userList: MutableSet<String> = mutableSetOf()

fun Route.apiRouter() {
    install(ContentNegotiation) {
        apiSerialization()
    }
    install(Sessions) {
        apiSessionsCookie()
    }
    application.install(Authentication) {
        form {
            skipWhen { it.sessions.get<ApiSession>()?.currentUserName != null }
            challenge { call.respond(HttpStatusCode.Unauthorized) }
            validate {
                when (it.password) {
                    "testing" -> UserIdPrincipal(it.name)
                    else -> null
                }
            }
        }
    }

    authenticate {
        post(ApiRoute.LOGIN.path) {
            val currentUser = call.principal<UserIdPrincipal>() ?: error ("No auth found")
            call.sessions.set(ApiSession(currentUserName = currentUser.name))
            call.respond(HttpStatusCode.OK)
        }

        post(ApiRoute.LOGOUT.path) {
            call.sessions.clear<ApiSession>()
            call.respond(HttpStatusCode.OK)
        }

        post(ApiRoute.USER_CREATE.path) {
            val request = call.receive<UserCreateRequest>()
            val error = if (userList.contains(request.userName)) {
                "User already created"
            } else {
                userList.add(request.userName)
                null
            }
            call.respond(UserCreateResponse(UserListResponse(userList.sorted()), error))
        }

        get(ApiRoute.USERS_LIST.path) {
            call.respond(UserListResponse(userList.sorted()))
        }

        route("{...}") {
            handle {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
