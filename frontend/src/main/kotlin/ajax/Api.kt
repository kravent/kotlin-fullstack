package ajax

import kotlinx.coroutines.await
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.browser.window

object Api {
    private val json = Json(JsonConfiguration.Default)

    suspend fun <T> get(path: String, deserializer: KSerializer<T>): T =
        window.fetch("/api/users").await().text().await().let { json.parse(deserializer, it) }
}
