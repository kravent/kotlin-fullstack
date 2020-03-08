package ajax

import component.store.LogoutStoreAction
import component.store.Store
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.ResponseException
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import me.agaman.kotlinfullstack.route.ApiRoute
import me.agaman.kotlinfullstack.route.Route
import utils.CsrfTokenHandler
import kotlin.browser.window

val client = HttpClient(Js) {
    install(JsonFeature) {
        serializer = ApiJsonSerializer()
    }
}

class ApiUnauthoridedException(response: HttpResponse) : ResponseException(response)
class ApiForbiddenException(response: HttpResponse) : ResponseException(response)

fun HttpRequestBuilder.localUrl(path: String) = url {
    takeFrom(window.location.href)
    encodedPath = path
}
fun HttpRequestBuilder.localUrl(route: Route) = localUrl(route.path)
fun HttpRequestBuilder.localUrl(apiRoute: ApiRoute) = localUrl("${Route.API.path}/${apiRoute.path}")

suspend inline fun <reified T> apiRequest(block: HttpRequestBuilder.() -> Unit): T =
    try {
        client.request {
            block()
            if (method != HttpMethod.Get) {
                header("X-CSRF", CsrfTokenHandler.getToken())
            }
        }
    } catch (e: Exception) {
        console.error(e)
        if (e is ClientRequestException) {
            when (e.response.status) {
                HttpStatusCode.Unauthorized -> {
                    Store.dispatch(LogoutStoreAction)
                    throw ApiUnauthoridedException(e.response)
                }
                HttpStatusCode.Forbidden -> {
                    // TODO show alert asking the user to reload the window
                    throw ApiUnauthoridedException(e.response)
                }
            }
        }
        throw e
    }

object Api {
    suspend fun login(user: String, password: String) {
        val csrfToken = apiRequest<String> {
            method = HttpMethod.Post
            localUrl(Route.LOGIN)
            body = FormDataContent(Parameters.build {
                set("user", user)
                set("password", password)
            })
        }
        CsrfTokenHandler.setToken(csrfToken)
    }

    suspend fun logout() {
        val csrfToken = apiRequest<String> {
            method = HttpMethod.Post
            localUrl(Route.LOGOUT)
        }
        CsrfTokenHandler.setToken(csrfToken)
    }

    suspend inline fun <reified T> get(apiRoute: ApiRoute): T = apiRequest {
        method = HttpMethod.Get
        localUrl(apiRoute)
    }

    suspend inline fun <reified T> rawPost(apiRoute: ApiRoute, noinline block: FormBuilder.() -> Unit): T = apiRequest {
        method = HttpMethod.Post
        localUrl(apiRoute)
        formData(block)
    }

    suspend inline fun <reified T> post(apiRoute: ApiRoute, data: Any): T = apiRequest {
        method = HttpMethod.Post
        localUrl(apiRoute)
        contentType(ContentType.Application.Json)
        body = data
    }
}
