package com.example.network.response

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@Serializable(with = ApiResponseSerializer::class)
data class ApiResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T? = null
)

class ApiResponseSerializer<T>(private val dataSerializer: KSerializer<T>) :
    KSerializer<ApiResponse<T>> {

    override val descriptor: SerialDescriptor = dataSerializer.descriptor

    override fun serialize(encoder: Encoder, value: ApiResponse<T>) {
        val json = Json.encodeToJsonElement(
            serializer(),
            mapOf(
                "isSuccess" to value.isSuccess,
                "code" to value.code,
                "message" to value.message,
                "result" to value.result
            )
        )
        encoder.encodeSerializableValue(Json.serializersModule.serializer(), json)
    }

    override fun deserialize(decoder: Decoder): ApiResponse<T> {
        val json = Json.decodeFromJsonElement<Map<String, Any?>>(
            serializer(),
            decoder.decodeSerializableValue(Json.serializersModule.serializer())
        )

        return ApiResponse(
            isSuccess = json["isSuccess"] as Boolean,
            code = json["code"] as String,
            message = json["message"] as String,
            result = json["result"] as? T
        )
    }
}
