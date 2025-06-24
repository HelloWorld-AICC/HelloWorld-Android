package com.example.core.domain.model

data class ChatLog(
    val content: String,
    val sender: String?,
    val showSummaryIcon: Boolean = true
)
