package com.example.core.data.model

data class AIChatLogResponse(
    val roomId : String,
    val chatLogs : List<AIChatMessage>
)

data class AIChatMessage(
    val content: String,
    val sender: String
)