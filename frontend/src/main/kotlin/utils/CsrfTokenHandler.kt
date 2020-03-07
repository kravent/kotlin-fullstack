package utils

import org.w3c.dom.HTMLInputElement
import kotlin.browser.document

object CsrfTokenHandler {
    private val csrfTokenInputElement
        get() = document.getElementById("_csrf_token")
            .let { it as HTMLInputElement }

    fun setToken(token: String) { csrfTokenInputElement.value = token }

    fun getToken(): String = csrfTokenInputElement.value
}
