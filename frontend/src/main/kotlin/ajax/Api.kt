package ajax

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import me.agaman.kotlinfullstack.route.ApiRoute
import me.agaman.kotlinfullstack.route.Route
import org.w3c.dom.get
import kotlin.browser.sessionStorage
import kotlin.browser.window

val client = HttpClient(Js) {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}

fun HttpRequestBuilder.url(apiRoute: ApiRoute) = url {
    takeFrom(sessionStorage["apiHost"] ?: window.location.href)
    encodedPath = "${Route.API.path}/${apiRoute.path}"
}

object Api {
    suspend inline fun <reified T> get(apiRoute: ApiRoute): T = try {
        client.request {
            method = HttpMethod.Get
            url(apiRoute)
        }
    } catch (e: Exception) {
        console.error(e)
        throw e
    }

    suspend inline fun <reified T> post(apiRoute: ApiRoute, data: Any): T = try {
        client.request {
            method = HttpMethod.Post
            url(apiRoute)
            body = data
            contentType(ContentType.Application.Json)
        }
    } catch (e: Exception) {
        console.error(e)
        throw e
    }
}
