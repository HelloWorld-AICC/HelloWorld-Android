package com.example.feature.ui.aichat.chatting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.model.aichat.AIChatMessage
import com.example.core.data.model.aichat.AIChatLogResponse
import com.example.core.data.network.RetrofitInstance
import com.example.network.response.ApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _chatRoomIds = MutableStateFlow<List<String>>(emptyList())
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
                Log.d(TAG, "🔵 요청: getAIChatLog($roomId)")
                val resp = RetrofitInstance.aiChatService.getAIChatLog(roomId) // Response<AIChatLogResponse>

                if (!resp.isSuccessful) {
                    Log.w(TAG, "❗getAIChatLog 실패: HTTP ${resp.code()}")
                    return@launch
                }

                val body: AIChatLogResponse = resp.body()
                    ?: run {
                        Log.w(TAG, "❗getAIChatLog 응답 body=null")
                        return@launch
                    }

                Log.d(TAG, "✅ 응답: roomId=${body.roomId}, chatLogs=${body.chatLogs.size}")

                val cleanedLogs: List<AIChatMessage> = body.chatLogs.map { m ->
                    // 서버가 user 메시지에 양끝 쿼트를 덧씌워 보내는 경우 방어
                    val cleaned = if (m.sender.equals("user", ignoreCase = true))
                        m.content.removeSurrounding("\"")
                    else m.content
                    AIChatMessage(content = cleaned, sender = m.sender)
                }

                // 첫 로드 시에만 인트로 메시지 앞에 붙이기 (중복 방지)
                val existing = _chatMessages.value[body.roomId].orEmpty()
                val intro = if (existing.isEmpty()) listOf(
                    AIChatMessage("안녕하세요!", sender = "bot"),
                    AIChatMessage("어떤 고민이 있으신가요?", sender = "bot")
                ) else emptyList()

                val finalMessages = intro + cleanedLogs

                _chatMessages.value = _chatMessages.value.toMutableMap().apply {
                    put(body.roomId, finalMessages)
                }

                if (!_chatRoomIds.value.contains(body.roomId)) {
                    _chatRoomIds.value = _chatRoomIds.value + body.roomId
                }
                _selectedChatId.value = body.roomId

                finalMessages.forEachIndexed { i, m ->
                    Log.d(TAG, "🗨️ $i : ${m.sender} → ${m.content}")
                }
            } catch (e: IOException) {
                Log.e(TAG, "네트워크 오류(getAIChatLog): ${e.message}", e)
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP 예외(getAIChatLog): ${e.message}", e)
            } catch (e: Exception) {
                Log.e(TAG, "알 수 없는 오류(getAIChatLog): ${e.message}", e)
            }
        }
    }

    fun sendUserMessage(chatId: String, userMessage: String) {
        val cur = _chatMessages.value[chatId].orEmpty()
        val updated = cur + AIChatMessage(content = userMessage, sender = "user")
        _chatMessages.value = _chatMessages.value.toMutableMap().apply { put(chatId, updated) }

        viewModelScope.launch {
            _isTyping.value = true
            var streamedText = ""
            var finalRoomId: String? = null

            Log.d(TAG, "💬 유저 메시지 전송 시작 → \"$userMessage\"")

            askToAIWithStream(
                roomId = chatId,
                message = userMessage,
                onPartialResponse = { partial ->
                    streamedText += partial
                    val curMsgs = _chatMessages.value[chatId].orEmpty()
                    val newList =
                        if (curMsgs.lastOrNull()?.sender == "bot")
                            curMsgs.dropLast(1) + AIChatMessage(streamedText, "bot")
                        else
                            curMsgs + AIChatMessage(streamedText, "bot")

                    _chatMessages.value = _chatMessages.value.toMutableMap().apply {
                        put(chatId, newList)
                    }
                    Log.d(TAG, "🔄 스트리밍 누적: $streamedText")
                },
                onComplete = { roomIdFromResponse ->
                    finalRoomId = roomIdFromResponse ?: chatId
                    _isTyping.value = false

                    if (chatId == "new_chat" && roomIdFromResponse != null) {
                        if (!_chatRoomIds.value.contains(roomIdFromResponse)) {
                            _chatRoomIds.value += roomIdFromResponse
                        }
                        val carry = _chatMessages.value["new_chat"].orEmpty()
                        _chatMessages.value = _chatMessages.value.toMutableMap().apply {
                            remove("new_chat")
                            put(roomIdFromResponse, carry)
                        }
                        _selectedChatId.value = roomIdFromResponse
                        Log.d(TAG, "✅ Room 이동: $finalRoomId")
                    } else {
                        _selectedChatId.value = finalRoomId
                    }

                    Log.d(TAG, "✅ 스트리밍 완료: finalRoomId=$finalRoomId")
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
            Log.d(TAG, "🌐 요청: askToAI(roomId=$roomId, message=$message)")

            // 서버가 단순 문자열 본문을 받는다면 text/plain 이 더 안전.
            // 만약 {"message": "..."} JSON을 요구한다면 DTO로 바꾸거나 실제 JSON으로 전송하세요.
            val requestBody = message.toRequestBody("text/plain".toMediaTypeOrNull())
            val resp = RetrofitInstance.aiChatService.askToAI(roomId, requestBody) // Response<ResponseBody>

            if (!resp.isSuccessful) {
                Log.w(TAG, "❗askToAI 실패: HTTP ${resp.code()}")
                onComplete(null)
                return
            }

            val source = resp.body()?.source()
            if (source == null) {
                Log.w(TAG, "❗askToAI 응답 body=null")
                onComplete(null); return
            }

            var roomIdFromStream: String? = null
            val builder = StringBuilder()
            var emptyDataCount = 0

            while (!source.exhausted()) {
                val line = source.readUtf8Line()
                Log.d(TAG, "📩 SSE 수신: $line")

                if (line != null && line.startsWith("data:")) {
                    val content = line.removePrefix("data:")
                    if (content.startsWith("Room ID:")) {
                        roomIdFromStream = content.removePrefix("Room ID:").trim()
                        Log.d(TAG, "🏷️ 추출 Room ID: $roomIdFromStream")
                    } else {
                        if (content.isEmpty()) {
                            emptyDataCount++
                            if (emptyDataCount == 2) {
                                builder.append("\n")
                                emptyDataCount = 0
                            }
                        } else {
                            emptyDataCount = 0
                            builder.append(content)
                        }
                    }
                } else if (line.isNullOrBlank()) {
                    val block = builder.toString().trimEnd()
                    if (block.isNotEmpty()) {
                        onPartialResponse(block)
                        builder.clear()
                    }
                }
            }

            onComplete(roomIdFromStream)
        } catch (e: Exception) {
            Log.e(TAG, "❌ askToAIWithStream 실패", e)
            onComplete(null)
        }
    }

    fun summarizeMessage() {
        val roomId = _selectedChatId.value ?: return
        viewModelScope.launch {
            try {
                Log.d(TAG, "📌 요약 요청: roomId=$roomId")
                // summaryAIChat: suspend fun summaryAIChat(roomId): Response<ResponseBody>
                val body = RetrofitInstance.aiChatService.summaryAIChat(roomId).body()
                    ?: return@launch
                val result = body.use { it.string() }  // "complete" 등 문자열
                Log.d(TAG, "✅ 요약 결과: $result")

                if (result.trim().equals("complete", ignoreCase = true)) {
                    _summaryCompleted.tryEmit(Unit)
                } else {
                    Log.w(TAG, "⚠️ 예상 외 응답: $result")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ 요약 요청 실패", e)
            }
        }
    }

    companion object {
        private const val TAG = "ChatViewModel"
    }
}
