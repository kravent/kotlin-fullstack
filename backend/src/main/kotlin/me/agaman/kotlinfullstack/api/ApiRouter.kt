package me.agaman.kotlinfullstack.api

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.serialization.serialization
import me.agaman.kotlinfullstack.model.UserCreateRequest
import me.agaman.kotlinfullstack.model.UserCreateResponse
import me.agaman.kotlinfullstack.model.UserListResponse
import me.agaman.kotlinfullstack.route.ApiRoute

private val userList: MutableSet<String> = mutableSetOf()

fun Route.apiRouter() {
    install(ContentNegotiation) {
        serialization()
    }

    install(CORS) {
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
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
