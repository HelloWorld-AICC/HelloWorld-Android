package com.example.feature.ui.aichat.chatting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.model.aichat.AIChatMessage
import com.example.core.data.network.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _chatRoomIds = MutableStateFlow<List<String>>(listOf())
    val chatRoomIds: StateFlow<List<String>> = _chatRoomIds

    private val _selectedChatId = MutableStateFlow<String?>(null)
    val selectedChatId: StateFlow<String?> = _selectedChatId

    private val _chatMessages = MutableStateFlow<Map<String, List<AIChatMessage>>>(emptyMap())
    val chatMessages: StateFlow<Map<String, List<AIChatMessage>>> = _chatMessages

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping

    private val _summaryCompleted = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val summaryCompleted = _summaryCompleted.asSharedFlow()

    fun loadChatLog(roomId: String) {
        viewModelScope.launch {
            try {
                Log.d("ChatViewModel", "🔵 요청: getAIChatLog($roomId)")
                val response = RetrofitInstance.aiChatService.getAIChatLog(roomId)
                Log.d("ChatViewModel", "✅ 응답 수신: chatLogs=${response.chatLogs.size}, roomId=${response.roomId}")

                val logs = response.chatLogs.map {
                    val cleanedContent = if (it.sender.lowercase() == "user") {
                        it.content.removeSurrounding("\"")
                    } else {
                        it.content
                    }
                    AIChatMessage(content = cleanedContent, sender = it.sender)
                }
                logs.forEachIndexed { i, log ->
                    Log.d("ChatViewModel", "🗨️ $i : ${log.sender} → ${log.content}")
                }

                val introMessages = listOf(
                    AIChatMessage(content = "안녕하세요!", sender = ""), // 언어에 따라 템플릿 다르게 변경 필요
                    AIChatMessage(content = "어떤 고민이 있으신가요?", sender = "") // 언어에 따라 템플릿 다르게 변경 필요
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
                Log.e("ChatViewModel", "❌ getAIChatLog 실패", e)
            }
        }
    }

    fun sendUserMessage(chatId: String, userMessage: String) {
        val currentMessages = _chatMessages.value[chatId].orEmpty()

        val newUserMessage = AIChatMessage(
            content = userMessage,
            sender = "user"
        )
        val updatedMessages = currentMessages + newUserMessage

        _chatMessages.value = _chatMessages.value.toMutableMap().apply {
            put(chatId, updatedMessages)
        }

        viewModelScope.launch {
            _isTyping.value = true

            var streamedText = ""
            var finalRoomId: String?

            Log.d("ChatViewModel", "💬 유저 메시지 전송 시작 → \"$userMessage\"")

            askToAIWithStream(
                roomId = chatId,
                message = userMessage,
                onPartialResponse = { partial ->
                    streamedText += partial

                    val current = _chatMessages.value[chatId].orEmpty()
                    val updated = if (current.lastOrNull()?.sender == "bot") {
                        current.dropLast(1) + AIChatMessage(content = streamedText, sender = "bot")
                    } else {
                        current + AIChatMessage(content = streamedText, sender = "bot")
                    }

                    _chatMessages.value = _chatMessages.value.toMutableMap().apply {
                        put(chatId, updated)
                    }

                    Log.d("ChatViewModel", "🔄 스트리밍 응답 누적: $streamedText")
                },
                onComplete = { roomIdFromResponse ->
                    _isTyping.value = false
                    finalRoomId = roomIdFromResponse ?: chatId

                    if (chatId == "new_chat" && roomIdFromResponse != null) {
                        if (!_chatRoomIds.value.contains(roomIdFromResponse)) {
                            _chatRoomIds.value += roomIdFromResponse
                        }

                        val currentMessages = _chatMessages.value["new_chat"].orEmpty()
                        _chatMessages.value = _chatMessages.value.toMutableMap().apply {
                            remove("new_chat")
                            put(roomIdFromResponse, currentMessages)
                        }

                        _selectedChatId.value = roomIdFromResponse
                        Log.d("ChatViewModel", "✅ Room 이동 완료: finalRoomId=$finalRoomId")
                    } else {
                        _selectedChatId.value = finalRoomId
                    }

                    Log.d("ChatViewModel", "✅ 스트리밍 완료: finalRoomId=$finalRoomId")
                }
            )
        }
    }

    private suspend fun askToAIWithStream(
        roomId: String,
        message: String,
        onPartialResponse: (String) -> Unit,
        onComplete: (String?) -> Unit
    ) {
        try {
            Log.d("ChatViewModel", "🌐 요청: askToAI(roomId=$roomId, message=$message)")

            val requestBody = message.toRequestBody("application/json".toMediaTypeOrNull())
            val response = RetrofitInstance.aiChatService.askToAI(roomId, requestBody)

            if (response.isSuccessful) {
                val source = response.body()?.source()
                var roomIdFromStream: String? = null
                val messageBuilder = StringBuilder()
                var emptyDataCount = 0

                while (!source!!.exhausted()) {
                    val line = source.readUtf8Line()
                    Log.d("ChatViewModel", "📩 SSE 수신: $line")

                    if (line != null && line.startsWith("data:")) {
                        val content = line.removePrefix("data:")

                        if (content.startsWith("Room ID:")) {
                            roomIdFromStream = content.removePrefix("Room ID:").trim()
                            Log.d("ChatViewModel", "🏷️ 추출된 Room ID: $roomIdFromStream")
                        } else {
                            if (content.isEmpty()) {
                                // 빈 data 줄 감지 (줄바꿈 의미)
                                emptyDataCount++
                                if (emptyDataCount == 2) {
                                    messageBuilder.append("\n")
                                    emptyDataCount = 0
                                }
                            } else {
                                emptyDataCount = 0
                                messageBuilder.append(content)
                            }
                        }
                    } else if (line.isNullOrBlank()) {
                        // 하나의 메시지 블록 종료
                        val completeMessage = messageBuilder.toString().trimEnd()
                        if (completeMessage.isNotEmpty()) {
                            onPartialResponse(completeMessage)
                            messageBuilder.clear()
                        }
                    }
                }

                onComplete(roomIdFromStream)
            } else {
                Log.w("ChatViewModel", "❗askToAI 응답 실패: code=${response.code()}")
                onComplete(null)
            }
        } catch (e: Exception) {
            Log.e("ChatViewModel", "❌ askToAIWithStream 실패", e)
            onComplete(null)
        }
    }

    fun summarizeMessage() {
        val roomId = _selectedChatId.value ?: return

        viewModelScope.launch {
            try {
                Log.d("ChatViewModel", "📌 요약 요청: roomId=$roomId")
                val body = RetrofitInstance.aiChatService.summaryAIChat(roomId)
                val result = body.use { it.string() }  // 꼭 close 되도록 use 사용
                Log.d("ChatViewModel", "✅ 요약 요청 성공: $result")

                if (result.trim().equals("complete", ignoreCase = true)) {
                    _summaryCompleted.tryEmit(Unit) // UI에서 다이얼로그 표시
                } else {
                    Log.w("ChatViewModel", "⚠️ 예상 외 응답: $result")
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "❌ 요약 요청 실패", e)
            }
        }
    }
}
