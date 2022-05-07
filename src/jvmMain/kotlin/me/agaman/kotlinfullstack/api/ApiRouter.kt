package me.agaman.kotlinfullstack.api

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import me.agaman.kotlinfullstack.model.UserCreateRequest
import me.agaman.kotlinfullstack.model.UserCreateResponse
import me.agaman.kotlinfullstack.model.UserListResponse
import me.agaman.kotlinfullstack.route.User

private val userList: MutableSet<String> = mutableSetOf()

fun Route.apiRouter() {
    post<User.Create> {
        val request = call.receive<UserCreateRequest>()
        val error = if (userList.contains(request.userName)) {
            "User already created"
        } else {
            userList.add(request.userName)
            null
        }
        call.respond(UserCreateResponse(UserListResponse(userList.sorted()), error))
    }

    get<User.List> {
        call.respond(UserListResponse(userList.sorted()))
    }
}
