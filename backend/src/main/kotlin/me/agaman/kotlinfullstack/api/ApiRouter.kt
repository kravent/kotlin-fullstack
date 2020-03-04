package me.agaman.kotlinfullstack.api

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import me.agaman.kotlinfullstack.api.utils.apiSerialization
import me.agaman.kotlinfullstack.model.UserCreateRequest
import me.agaman.kotlinfullstack.model.UserCreateResponse
import me.agaman.kotlinfullstack.model.UserListResponse
import me.agaman.kotlinfullstack.route.ApiRoute

private val userList: MutableSet<String> = mutableSetOf()

fun Route.apiRouter() {
    install(ContentNegotiation) {
        apiSerialization()
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
