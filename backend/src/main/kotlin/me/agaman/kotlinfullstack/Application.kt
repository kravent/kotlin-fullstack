package me.agaman.kotlinfullstack

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import me.agaman.kotlinfullstack.api.apiRouter

private const val REACT_PAGE = """
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Kotlin fullstack</title>
</head>
<body>
<div id="root"></div>
<script src="static/app.js"></script>
</body>
</html>
"""

private suspend fun ApplicationCall.respondReactPage() =
    respondText(REACT_PAGE, ContentType.Text.Html)

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        route("/api") {
            apiRouter()
        }
        static("/static") {
            resources("static")
        }
        get("/") {
            call.respondReactPage()
        }
        get("*") {
            call.respondReactPage()
        }
    }
}
