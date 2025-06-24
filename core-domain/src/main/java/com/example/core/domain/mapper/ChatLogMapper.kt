package com.example.core.domain.mapper

import com.example.core.data.model.AIChatLogResponse
import com.example.core.domain.model.ChatLog

fun fromResponse(response: AIChatLogResponse): List<ChatLog> {
    return response.chatLogs.map {
        ChatLog(
            content = it.content,
            sender = it.sender,
            showSummaryIcon = true  // 기본값 예시
        )
    }
}
