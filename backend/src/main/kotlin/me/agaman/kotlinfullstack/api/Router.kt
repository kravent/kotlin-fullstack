package me.agaman.kotlinfullstack.api

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.apiRouter() {
    get("/users") {
        call.respondText { "{\"users\":[]}" }
    }
    get("*") {
        call.respond(HttpStatusCode.NotFound)
    }
}
