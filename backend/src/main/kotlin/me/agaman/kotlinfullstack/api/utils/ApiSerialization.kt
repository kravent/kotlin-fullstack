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
import kotlinx.serialization.serializer
import me.agaman.kotlinfullstack.Serializer

/**
 * TODO Substitute by serialization() from ktor-serialization
 */
fun ContentNegotiation.Configuration.apiSerialization() {
    val converter = ApiSerializationConverter()
    register(ContentType.Application.Json, converter)
}

private class ApiSerializationConverter : ContentConverter {
    @KtorExperimentalAPI
    @ImplicitReflectionSerializer
    override suspend fun convertForSend(
        context: PipelineContext<Any, ApplicationCall>,
        contentType: ContentType,
        value: Any
    ): Any? {
        val content = Serializer.json.stringify(Serializer.serializerFor(value), value)
        return TextContent(content, contentType.withCharset(context.call.suitableCharset()))
    }

    @KtorExperimentalAPI
    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
        val request = context.subject
        val channel = request.value as? ByteReadChannel ?: return null
        val charset = context.call.request.contentCharset() ?: Charsets.UTF_8
        val content = channel.readRemaining().readText(charset)

        return Serializer.json.parse(serializer(request.typeInfo), content)
    }
}

