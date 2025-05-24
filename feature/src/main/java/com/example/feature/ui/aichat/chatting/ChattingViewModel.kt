package com.example.feature.ui.aichat.chatting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val showSummaryIcon: Boolean = true  // 기본값은 true
)

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    // 채팅방 목록 (예: 1, 2, 3)
    private val _chatRoomIds = MutableStateFlow<List<Int>>(listOf())
    val chatRoomIds: StateFlow<List<Int>> = _chatRoomIds

    // 선택된 채팅방 ID
    private val _selectedChatId = MutableStateFlow<Int?>(null)
    val selectedChatId: StateFlow<Int?> = _selectedChatId

    // 각 채팅방 ID에 대응하는 메시지들
    private val _chatMessages = MutableStateFlow<Map<Int, List<ChatMessage>>>(emptyMap())
    val chatMessages: StateFlow<Map<Int, List<ChatMessage>>> = _chatMessages

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping

    /** 채팅방 선택 **/
    fun selectChat(chatId: Int) {
        _selectedChatId.value = chatId

        // 새 채팅방이면 초기 메시지 추가
        if (!_chatMessages.value.containsKey(chatId)) {
            val initialMessages = listOf(
                ChatMessage(text = "안녕하세요!", isUser = false, showSummaryIcon = false),
                ChatMessage(text = "어떤 고민이 있으신가요?", isUser = false, showSummaryIcon = false)
            )

            _chatMessages.value = _chatMessages.value.toMutableMap().apply {
                put(chatId, initialMessages)
            }

            // 채팅방 ID 목록에 추가
            _chatRoomIds.value = _chatRoomIds.value + chatId
        }
    }

    /** 메시지 전송 **/
    fun sendUserMessage(chatId: Int, userMessage: String) {
        val currentMessages = _chatMessages.value[chatId].orEmpty()

        // 유저 메시지 추가
        val newUserMessage = ChatMessage(
            text = userMessage,
            isUser = true
        )
        val updatedMessages = currentMessages + newUserMessage

        _chatMessages.value = _chatMessages.value.toMutableMap().apply {
            put(chatId, updatedMessages)
        }

        // AI 응답 추가
        viewModelScope.launch {
            _isTyping.value = true  // ⬅️ 입력 중 표시 ON

            val response = callAI(userMessage)

            _isTyping.value = false // ⬅️ 입력 중 표시 OFF

            val refreshedMessages = _chatMessages.value[chatId].orEmpty()
            val aiMessage = ChatMessage(
                text = response,
                isUser = false
            )

            val finalMessages = refreshedMessages + aiMessage

            _chatMessages.value = _chatMessages.value.toMutableMap().apply {
                put(chatId, finalMessages)
            }
        }
    }

    /** 메시지 가져오기 **/
    fun getMessages(chatId: Int): List<ChatMessage> {
        return _chatMessages.value[chatId].orEmpty()
    }

    private suspend fun callAI(prompt: String): String {
        delay(10000)
        return "AI 응답: $prompt"
    }

    fun summarizeMessage(text : String) : Boolean {
        return true
    }

}
