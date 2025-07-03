package com.example.core.data.model.aichat

data class AIChatLogResponse(
    val roomId : String,
    val chatLogs : List<AIChatMessage>
)

data class AIChatMessage(
    val content: String,
    val sender: String
)