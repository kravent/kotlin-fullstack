package me.agaman.kotlinfullstack.api.utils

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.ContentConverter
import io.ktor.features.ContentNegotiation
import io.ktor.features.suitableCharset
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.contentCharset
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.pipeline.PipelineContext
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.readText
import io.ktor.utils.io.readRemaining
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonElementSerializer
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

/**
 * TODO Substitute by serialization() from ktor-serialization
 */
fun ContentNegotiation.Configuration.apiSerialization() {
    val converter = ApiSerializationConverter()
    register(ContentType.Application.Json, converter)
}

private class ApiSerializationConverter : ContentConverter {
    private val json = Json(JsonConfiguration.Stable)

    @ImplicitReflectionSerializer
    @KtorExperimentalAPI
    @InternalSerializationApi
    override suspend fun convertForSend(
        context: PipelineContext<Any, ApplicationCall>,
        contentType: ContentType,
        value: Any
    ): Any? {
        @Suppress("UNCHECKED_CAST")
        val content = json.stringify(serializerForSending(value) as KSerializer<Any>, value)
        return TextContent(content, contentType.withCharset(context.call.suitableCharset()))
    }

    @KtorExperimentalAPI
    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
        val request = context.subject
        val channel = request.value as? ByteReadChannel ?: return null
        val charset = context.call.request.contentCharset() ?: Charsets.UTF_8

        val content = channel.readRemaining().readText(charset)
        val serializer = serializerByTypeInfo(request.typeInfo)

        return json.parse(serializer, content)
    }

    @ImplicitReflectionSerializer
    @InternalSerializationApi
    private fun serializerForSending(value: Any): KSerializer<*> {
        if (value is JsonElement) {
            return JsonElementSerializer
        }
        if (value is List<*>) {
            return ListSerializer(value.elementSerializer())
        }
        if (value is Set<*>) {
            return SetSerializer(value.elementSerializer())
        }
        if (value is Map<*, *>) {
            return MapSerializer(value.keys.elementSerializer(), value.values.elementSerializer())
        }
        if (value is Map.Entry<*, *>) {
            return MapEntrySerializer(
                serializerForSending(value.key ?: error("Map.Entry(null, ...) is not supported")),
                serializerForSending(value.value ?: error("Map.Entry(..., null) is not supported)"))
            )
        }
        if (value is Array<*>) {
            val componentType = value.javaClass.componentType.kotlin.starProjectedType
            val componentClass =
                componentType.classifier as? KClass<*> ?: error("Unsupported component type $componentType")
            @Suppress("UNCHECKED_CAST")
            return ArraySerializer(
                componentClass as KClass<Any>,
                serializerByTypeInfo(componentType) as KSerializer<Any>
            )
        }
        return value::class.serializer()
    }

    private fun serializerByTypeInfo(type: KType): KSerializer<*> {
        val classifierClass = type.classifier as? KClass<*>
        if (classifierClass != null && classifierClass.java.isArray) {
            return arraySerializer(type)
        }

        return serializer(type)
    }

    // NOTE: this should be removed once kotlinx.serialization serializer get support of arrays that is blocked by KT-32839
    private fun arraySerializer(type: KType): KSerializer<*> {
        val elementType = type.arguments[0].type ?: error("Array<*> is not supported")
        val elementSerializer = serializerByTypeInfo(elementType)

        @Suppress("UNCHECKED_CAST")
        return ArraySerializer(
            elementType.jvmErasure as KClass<Any>,
            elementSerializer as KSerializer<Any>
        )
    }

    @ImplicitReflectionSerializer
    @InternalSerializationApi
    private fun Collection<*>.elementSerializer(): KSerializer<*> {
        @Suppress("DEPRECATION_ERROR")
        val serializers = mapNotNull { value ->
            value?.let { serializerForSending(it) }
        }.distinctBy { it.descriptor.name }

        if (serializers.size > 1) {
            @Suppress("DEPRECATION_ERROR")
            error("Serializing collections of different element types is not yet supported. " +
                    "Selected serializers: ${serializers.map { it.descriptor.name }}"
            )
        }

        val selected: KSerializer<*> = serializers.singleOrNull() ?: String.serializer()
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

