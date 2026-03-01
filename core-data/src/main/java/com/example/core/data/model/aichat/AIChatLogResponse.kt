package com.example.core.data.model.aichat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AIChatLogResponse(
    @SerialName("timestamp") val timestamp: String,
    @SerialName("path") val path: String,
    @SerialName("status") val status: Int,
    @SerialName("error") val error: String,
    @SerialName("requestId") val requestId: String,
    @SerialName("data") val data: AIChatLogData
)

@Serializable
data class AIChatLogData(
    @SerialName("roomId") val roomId: String,
    @SerialName("chatLogs") val chatLogs: List<AIChatMessage>
)

@Serializable
data class AIChatMessage(
    @SerialName("content") val content: String,
    @SerialName("sender") val sender: String
)
