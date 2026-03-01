package com.example.core.data.model.aichat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AIChatSseEvent(
    @SerialName("type") val type: String,
    @SerialName("content") val content: String? = null
)
