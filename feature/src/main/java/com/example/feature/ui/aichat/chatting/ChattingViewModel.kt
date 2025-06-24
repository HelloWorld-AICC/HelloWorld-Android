package com.example.feature.ui.aichat.chatting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.network.RetrofitInstance
import com.example.core.domain.mapper.fromResponse
import com.example.core.domain.model.ChatLog
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _chatRoomIds = MutableStateFlow<List<String>>(listOf())
    val chatRoomIds: StateFlow<List<String>> = _chatRoomIds

    private val _selectedChatId = MutableStateFlow<String?>(null)
    val selectedChatId: StateFlow<String?> = _selectedChatId

    private val _chatMessages = MutableStateFlow<Map<String, List<ChatLog>>>(emptyMap())
    val chatMessages: StateFlow<Map<String, List<ChatLog>>> = _chatMessages

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping

    fun loadChatLog(roomId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.aiChatService.getAIChatLog(roomId)
                val logs = fromResponse(response)

                // ✅ 항상 기본 메시지를 맨 앞에 삽입
                val introMessages = listOf(
                    ChatLog(content = "안녕하세요!", sender = null, showSummaryIcon = false),
                    ChatLog(content = "어떤 고민이 있으신가요?", sender = null, showSummaryIcon = false)
                )
                val finalMessages = introMessages + logs

                _chatMessages.value = _chatMessages.value.toMutableMap().apply {
                    put(roomId, finalMessages)
                }

                if (!_chatRoomIds.value.contains(roomId)) {
                    _chatRoomIds.value = _chatRoomIds.value + roomId
                }

                _selectedChatId.value = roomId
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendUserMessage(chatId: String, userMessage: String) {
        val currentMessages = _chatMessages.value[chatId].orEmpty()

        val newUserMessage = ChatLog(
            content = userMessage,
            sender = "user",
            showSummaryIcon = false
        )
        val updatedMessages = currentMessages + newUserMessage

        _chatMessages.value = _chatMessages.value.toMutableMap().apply {
            put(chatId, updatedMessages)
        }

        viewModelScope.launch {
            _isTyping.value = true

            val responseText = callAI(chatId, userMessage)

            _isTyping.value = false

            val refreshedMessages = _chatMessages.value[chatId].orEmpty()
            val aiMessage = ChatLog(
                content = responseText,
                sender = "ai",
                showSummaryIcon = true
            )

            _chatMessages.value = _chatMessages.value.toMutableMap().apply {
                put(chatId, refreshedMessages + aiMessage)
            }
        }
    }

    private suspend fun callAI(roomId: String, prompt: String): String {
        return try {
            val response = RetrofitInstance.aiChatService.askToAI(
                roomId = roomId,
                request = prompt
            )

            if (response.isSuccessful) {
                response.body()?.string() ?: "응답이 비어 있습니다."
            } else {
                "AI 응답 실패: ${response.code()}"
            }
        } catch (e: Exception) {
            "에러 발생: ${e.message}"
        }
    }

    fun summarizeMessage(text: String): Boolean = true
}
