package utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

object Serializer {
    val json = Json(JsonConfiguration.Stable)
}
