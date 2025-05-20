package com.example.feature.ui.aichat.chatting

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import jakarta.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class ChatMessage(val text: String, val isUser: Boolean, val timestamp: Long)

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun sendUserMessage(userMessage: String) {
        val updated = _messages.value + ChatMessage(userMessage, true, System.currentTimeMillis())
        _messages.value = updated

        viewModelScope.launch {
            val response = callAI(userMessage) // 여기에 실제 AI 호출
            _messages.value = _messages.value + ChatMessage(response, false, System.currentTimeMillis())
        }
    }

    private suspend fun callAI(prompt: String): String {
        delay(1000) // 실제 AI 응답 대기
        return "AI 응답: $prompt" // 실제로는 API 결과
    }
}
