package ajax

import io.ktor.client.call.TypeInfo
import io.ktor.client.features.json.JsonSerializer
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.utils.io.core.Input
import io.ktor.utils.io.core.readText
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.serializer
import me.agaman.kotlinfullstack.Serializer

/**
 * TODO Substitute with KotlinxSerializer from ktor-client-serialization-js
 */
class ApiJsonSerializer : JsonSerializer {
    @ImplicitReflectionSerializer
    override fun write(data: Any, contentType: ContentType): OutgoingContent {
        val content = Serializer.json.stringify(Serializer.serializerFor(data), data)
        return TextContent(content, contentType)
    }

    @ImplicitReflectionSerializer
    override fun read(type: TypeInfo, body: Input): Any {
        val text = body.readText()
        val mapper = type.kotlinType?.let { serializer(it) } ?: type.type.serializer()
        return Serializer.json.parse(mapper, text)!!
    }
}
