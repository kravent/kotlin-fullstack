package me.agaman.kotlinfullstack.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import me.agaman.kotlinfullstack.model.UserCreateRequest
import me.agaman.kotlinfullstack.model.UserCreateResponse
import me.agaman.kotlinfullstack.model.UserListResponse
import me.agaman.kotlinfullstack.route.ApiRoute

private val userList: MutableSet<String> = mutableSetOf()

fun Route.apiRouter() {
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
