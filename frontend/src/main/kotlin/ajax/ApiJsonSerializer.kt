package ajax

import io.ktor.client.call.TypeInfo
import io.ktor.client.features.json.JsonSerializer
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.utils.io.core.Input
import io.ktor.utils.io.core.readText
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonElementSerializer
import kotlinx.serialization.serializer
import utils.Serializer

/**
 * TODO Substitute with KotlinxSerializer from ktor-client-serialization-js
 */
class ApiJsonSerializer : JsonSerializer {
    @ImplicitReflectionSerializer
    override fun write(data: Any, contentType: ContentType): OutgoingContent {
        @Suppress("UNCHECKED_CAST")
        val content = Serializer.json.stringify(buildSerializer(data) as KSerializer<Any>, data)
        return TextContent(content, contentType)
    }

    @ImplicitReflectionSerializer
    override fun read(type: TypeInfo, body: Input): Any {
        val text = body.readText()
        val mapper = type.kotlinType?.let { serializer(it) } ?: type.type.serializer()
        return Serializer.json.parse(mapper, text)!!
    }

    @ImplicitReflectionSerializer
    private fun buildSerializer(value: Any): KSerializer<*> = when (value) {
        is JsonElement -> JsonElementSerializer
        is List<*> -> value.elementSerializer().list
        is Array<*> -> value.firstOrNull()?.let { buildSerializer(it) } ?: String.serializer().list
        is Set<*> -> value.elementSerializer().set
        is Map<*, *> -> {
            val keySerializer = value.keys.elementSerializer()
            val valueSerializer = value.values.elementSerializer()

            MapSerializer(keySerializer, valueSerializer)
        }
        else -> value::class.serializer()
    }

    @ImplicitReflectionSerializer
    private fun Collection<*>.elementSerializer(): KSerializer<*> {
        @Suppress("DEPRECATION_ERROR")
        val serializers = filterNotNull().map { buildSerializer(it) }.distinctBy { it.descriptor.name }

        if (serializers.size > 1) {
            @Suppress("DEPRECATION_ERROR")
            error(
                "Serializing collections of different element types is not yet supported. " +
                        "Selected serializers: ${serializers.map { it.descriptor.name }}"
            )
        }

        val selected = serializers.singleOrNull() ?: String.serializer()

        if (selected.descriptor.isNullable) {
            return selected
        }

        @Suppress("UNCHECKED_CAST")
        selected as KSerializer<Any>

        if (any { it == null }) {
            return selected.nullable
        }

        return selected
    }
}
