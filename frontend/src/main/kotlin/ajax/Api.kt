package ajax

import component.store.LogoutStoreAction
import component.store.Store
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.request
import io.ktor.http.*
import me.agaman.kotlinfullstack.route.ApiRoute
import me.agaman.kotlinfullstack.route.Route
import kotlin.browser.window

val client = HttpClient(Js) {
    install(JsonFeature) {
        serializer = ApiJsonSerializer()
    }
}

fun HttpRequestBuilder.url(apiRoute: ApiRoute) = url {
    takeFrom(window.location.href)
    encodedPath = "${Route.API.path}/${apiRoute.path}"
}

suspend inline fun <reified T> apiRequest(block: HttpRequestBuilder.() -> Unit): T =
    try {
        client.request(block)
    } catch (e: Exception) {
        if (e is ClientRequestException && e.response.status == HttpStatusCode.Unauthorized) {
            Store.dispatch(LogoutStoreAction)
        }
        console.error(e)
        throw e
    }

object Api {
    suspend fun login(user: String, password: String) {
        apiRequest<String> {
            method = HttpMethod.Post
            url(ApiRoute.LOGIN)
            body = FormDataContent(Parameters.build {
                set("user", user)
                set("password", password)
            })
        }
    }

    suspend fun logout() {
        apiRequest<String> {
            method = HttpMethod.Post
            url(ApiRoute.LOGOUT)
        }
    }

    suspend inline fun <reified T> get(apiRoute: ApiRoute): T = apiRequest {
        method = HttpMethod.Get
        url(apiRoute)
    }

    suspend inline fun <reified T> rawPost(apiRoute: ApiRoute, noinline block: FormBuilder.() -> Unit): T = apiRequest {
        method = HttpMethod.Post
        url(apiRoute)
        formData(block)
    }

    suspend inline fun <reified T> post(apiRoute: ApiRoute, data: Any): T = apiRequest {
        method = HttpMethod.Post
        url(apiRoute)
        body = data
        contentType(ContentType.Application.Json)
    }
}
