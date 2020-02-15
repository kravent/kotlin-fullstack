package ajax

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import io.ktor.http.takeFrom
import kotlin.browser.window

val client = HttpClient(Js) {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}

object Api {
    suspend inline fun <reified T> get(path: String): T = client.request {
        method = HttpMethod.Get
        url {
            takeFrom(window.location.href)
            encodedPath = path
        }
    }

    suspend inline fun <reified T> post(path: String, data: Any): T = client.request {
        method = HttpMethod.Post
        url {
            takeFrom(window.location.href)
            encodedPath = path
        }
        body = data
    }
}
