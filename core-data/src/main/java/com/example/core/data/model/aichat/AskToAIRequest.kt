package com.example.core.data.model.aichat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AskToAIRequest(
    @SerialName("query") val query: String
)
