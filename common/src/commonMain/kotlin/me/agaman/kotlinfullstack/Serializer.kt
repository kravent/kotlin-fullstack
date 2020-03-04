package me.agaman.kotlinfullstack

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonElementSerializer
import kotlinx.serialization.serializer

object Serializer {
    val json = Json(JsonConfiguration.Stable)

    @Suppress("UNCHECKED_CAST")
    @ImplicitReflectionSerializer
    fun serializerFor(value: Any): KSerializer<Any> = buildSerializer(value) as KSerializer<Any>

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
        val serializers = filterNotNull().map { buildSerializer(it) }.distinctBy { it.descriptor.serialName }

        if (serializers.size > 1) {
            error(
                "Serializing collections of different element types is not yet supported. " +
                        "Selected serializers: ${serializers.map { it.descriptor.serialName }}"
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
