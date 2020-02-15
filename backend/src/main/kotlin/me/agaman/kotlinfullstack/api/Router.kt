package me.agaman.kotlinfullstack.api

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.serialization.serialization
import me.agaman.kotlinfullstack.model.UsersResponse

fun Route.apiRouter() {
    install(ContentNegotiation) {
        serialization()
    }

    get("/users") {
        call.respond(UsersResponse(listOf("user1", "user2")))
    }
    get("*") {
        call.respond(HttpStatusCode.NotFound)
    }
}
