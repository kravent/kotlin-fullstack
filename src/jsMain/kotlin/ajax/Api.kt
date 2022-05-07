package ajax

import ajax.features.Csrf
import component.store.LogoutStoreAction
import component.store.Store
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import utils.CsrfTokenHandler
import me.agaman.kotlinfullstack.route.Api as ApiRoute

object Api {
    val client = HttpClient(Js) {
        install(Resources)
        install(ContentNegotiation) {
            json()
        }
        install(Csrf) {
            csrfProvider = { CsrfTokenHandler.getToken() }
        }
        HttpResponseValidator {
            validateResponse { response ->
                when (response.status) {
                    HttpStatusCode.Unauthorized -> {
                        Store.dispatch(LogoutStoreAction)
                        throw ApiUnauthoridedException(response)
                    }
                    HttpStatusCode.Forbidden -> {
                        // TODO show alert asking the user to reload the window
                        throw ApiForbiddenException(response)
                    }
                }
                if (!response.status.isSuccess()) {
                    throw ApiException(response, response.status.description)
                }
            }
        }
    }

    suspend fun login(user: String, password: String) {
        val csrfToken: String = client.post(ApiRoute.Login()) {
            setBody(FormDataContent(Parameters.build {
                set("user", user)
                set("password", password)
            }))
        }.body()
        CsrfTokenHandler.setToken(csrfToken)
    }

    suspend fun logout() {
        val csrfToken: String = client.post(ApiRoute.Logout()).body()
        CsrfTokenHandler.setToken(csrfToken)
    }

    suspend inline fun <reified T : Any> get(resource: T): HttpResponse {
        return client.get(resource)
    }

    suspend inline fun <reified T : Any> post(resource: T): HttpResponse {
        return client.post(resource)
    }

    suspend inline fun <reified T : Any, reified B> post(resource: T, bodyContent: B): HttpResponse {
        return client.post(resource) {
            contentType(ContentType.Application.Json)
            setBody(bodyContent)
        }
    }
}

class ApiUnauthoridedException(response: HttpResponse) : ResponseException(response, "Unauthorided")
class ApiForbiddenException(response: HttpResponse) : ResponseException(response, "Forbidden")
class ApiException(response: HttpResponse, text: String) : ResponseException(response, text)
