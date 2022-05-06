package ajax

import component.store.LogoutStoreAction
import component.store.Store
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.window
import me.agaman.kotlinfullstack.route.ApiRoute
import me.agaman.kotlinfullstack.route.Route
import utils.CsrfTokenHandler

val client = HttpClient(Js) {
    install(ContentNegotiation) {
        json()
    }
}

class ApiUnauthoridedException(response: HttpResponse) : ResponseException(response, "Unauthorided")
class ApiForbiddenException(response: HttpResponse) : ResponseException(response, "Forbidden")

fun HttpRequestBuilder.localUrl(path: String) = url {
    takeFrom(window.location.href)
    encodedPath = path
}
fun HttpRequestBuilder.localUrl(route: Route) = localUrl(route.path)
fun HttpRequestBuilder.localUrl(apiRoute: ApiRoute) = localUrl("${Route.API.path}/${apiRoute.path}")

suspend inline fun <reified T> apiRequest(requestConfigurator: HttpRequestBuilder.() -> Unit): T =
    try {
        client.request {
            requestConfigurator()
            if (method != HttpMethod.Get) {
                header("X-CSRF", CsrfTokenHandler.getToken())
            }
        }.body()
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
                    throw ApiForbiddenException(e.response)
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
            setBody(FormDataContent(Parameters.build {
                set("user", user)
                set("password", password)
            }))
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

    suspend inline fun <reified T> rawPost(apiRoute: ApiRoute, noinline formBuilder: FormBuilder.() -> Unit): T = apiRequest {
        method = HttpMethod.Post
        localUrl(apiRoute)
        formData(formBuilder)
    }

    suspend inline fun <reified T> post(apiRoute: ApiRoute, data: Any): T = apiRequest {
        method = HttpMethod.Post
        localUrl(apiRoute)
        contentType(ContentType.Application.Json)
        setBody(data)
    }
}
