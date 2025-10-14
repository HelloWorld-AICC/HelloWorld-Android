package com.example.core.data.model.aichat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AIChatLogResponse(
    @SerialName("roomId") val roomId : String,
    @SerialName("chatLogs") val chatLogs : List<AIChatMessage>
)

@Serializable
data class AIChatMessage(
    @SerialName("content") val content: String,
    @SerialName("sender") val sender: String
)