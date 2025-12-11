package com.example.feature.ui.aichat.chatting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.model.aichat.AIChatMessage
import com.example.core.data.model.aichat.AIChatLogResponse
import com.example.core.data.network.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.EOFException
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
                val resp = RetrofitInstance.aiChatService.getAIChatLog(roomId)

                if (!resp.isSuccessful) {
                    Log.w(TAG, "❗getAIChatLog 실패: HTTP ${resp.code()}")
                    return@launch
                }

                val body: AIChatLogResponse = resp.body() ?: run {
                    Log.w(TAG, "❗getAIChatLog 응답 body=null")
                    return@launch
                }

                Log.d(TAG, "✅ 응답: roomId=${body.roomId}, chatLogs=${body.chatLogs.size}")

                val cleanedLogs: List<AIChatMessage> = body.chatLogs.map { m ->
                    val base = if (m.sender.equals("user", ignoreCase = true))
                        m.content.removeSurrounding("\"")
                    else
                        m.content
                    val normalized = normalizeServerText(base)
                    Log.d("Response", base)
                    AIChatMessage(content = normalized, sender = m.sender)
                }

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
                    // 들어온 조각을 곧바로 누적하여 마지막 bot 말풍선에 반영
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

    /**
     * 서버가 text/event-stream 형식으로 "data: ..." 라인을 지속 전송한다고 가정.
     * 라인 단위로 즉시 onPartialResponse를 호출하여 UI가 실시간 반영되도록 함.
     */
    /**
     * 서버가 text/event-stream 형식으로 "data: ..." 라인을 지속 전송한다고 가정.
     * - `data:`(빈값)  → PASS (누적 안 함)
     * - `data:   `     → 공백 n칸 누적 (2칸=LF1, 홀수는 스페이스1)
     * - `data:  -`     → 선행 공백 누적 후 문자 토큰 방출
     */
    private suspend fun askToAIWithStream(
        roomId: String,
        message: String,
        onPartialResponse: (String) -> Unit,
        onComplete: (String?) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🌐 요청: askToAI(roomId=$roomId, message=$message)")
            val requestBody = message.toRequestBody("text/plain".toMediaTypeOrNull())
            val resp = RetrofitInstance.aiChatService.askToAI(roomId, requestBody)

            if (!resp.isSuccessful) {
                Log.w(TAG, "❗askToAI 실패: HTTP ${resp.code()}")
                withContext(Dispatchers.Main) { onComplete(null) }
                return@withContext
            }

            val body = resp.body()
            val source = body?.source()
            if (source == null) {
                Log.w(TAG, "❗askToAI 응답 body=null")
                withContext(Dispatchers.Main) { onComplete(null) }
                return@withContext
            }

            var roomIdFromStream: String? = null

            // 공백 누적 버퍼
            var pendingSpaces = 0        // ' ' 개수
            var pendingNewlines = 0      // '\n' 개수 (공백 2칸 = 개행 1개)

            fun accumulateSpaces(n: Int) {
                if (n <= 0) return
                pendingNewlines += (n / 2)
                pendingSpaces   += (n % 2)
            }
            fun flushPrefix(): String {
                val prefix = buildString {
                    if (pendingNewlines > 0) append("\n".repeat(pendingNewlines))
                    if (pendingSpaces   > 0) append(" ".repeat(pendingSpaces))
                }
                pendingNewlines = 0
                pendingSpaces = 0
                return prefix
            }

            try {
                while (!source.exhausted()) {
                    val line = try { source.readUtf8Line() } catch (e: EOFException) { null }
                    if (line == null) break

                    Log.d(TAG, "📩 SSE 수신: $line")

                    if (!line.startsWith("data:")) {
                        // data:가 아닌 줄은 무시(서버 경계 빈줄은 이미 위 규칙으로 처리됨)
                        continue
                    }

                    // "data:" 이후 문자열 (공백 보존)
                    val after = if (line.length > 5) line.substring(5) else ""

                    // 종료 토큰
                    if (after == "[DONE]" || after == "[COMPLETE]") break

                    // Room ID
                    if (after.startsWith("Room ID:")) {
                        roomIdFromStream = after.removePrefix("Room ID:").trim()
                        Log.d(TAG, "🏷️ 추출 Room ID: $roomIdFromStream")
                        continue
                    }

                    // ✅ 빈값 → LF 1개 누적
                    if (after.isEmpty()) {
                        pendingNewlines += 1
                        continue
                    }

                    // ✅ 전부 공백이면 길이만큼 누적(짝수=LF, 홀수=스페이스)
                    if (after.all { it == ' ' }) {
                        accumulateSpaces(after.length)
                        continue
                    }

                    // ✅ 혼합 토큰: 선행 공백 누적 후 나머지 텍스트 방출
                    val firstNonSpaceIdx = after.indexOfFirst { it != ' ' }.let { if (it == -1) after.length else it }
                    if (firstNonSpaceIdx > 0) accumulateSpaces(firstNonSpaceIdx)

                    val rest = after.drop(firstNonSpaceIdx)
                    if (rest.isEmpty()) continue

                    val normalized = normalizeServerText(rest)

                    withContext(Dispatchers.Main) {
                        onPartialResponse(flushPrefix() + normalized)
                    }
                }

            } finally {
                body.close()
            }

            // 스트림 종료 시 남은 버퍼 방출(선택)
            if (pendingNewlines > 0 || pendingSpaces > 0) {
                val tail = "\n".repeat(pendingNewlines) + " ".repeat(pendingSpaces)
                withContext(Dispatchers.Main) { onPartialResponse(tail) }
            }

            withContext(Dispatchers.Main) {
                onComplete(roomIdFromStream)
            }
        } catch (ce: CancellationException) {
            Log.w(TAG, "⚠️ 스트림 취소됨")
            withContext(Dispatchers.Main) { onComplete(null) }
        } catch (e: Exception) {
            Log.e(TAG, "❌ askToAIWithStream 실패", e)
            withContext(Dispatchers.Main) { onComplete(null) }
        }
    }

    fun summarizeMessage() {
        val roomId = _selectedChatId.value ?: return
        viewModelScope.launch {
            try {
                Log.d(TAG, "📌 요약 요청: roomId=$roomId")
                val body = RetrofitInstance.aiChatService.summaryAIChat(roomId).body() ?: return@launch
                val result = body.use { it.string() }
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

    private fun normalizeServerText(raw: String): String {
        // 1) 두 번 이스케이프된 \\n, \\r 를 실제 개행으로
        // 2) CRLF/CR 을 모두 LF 로
        // 3) 탭을 공백으로(선택)
        return raw
            .replace("\\r\\n", "\n")   // "\\r\\n" -> "\n"
            .replace("\\n", "\n")      // "\\n"    -> "\n"
            .replace("\\r", "\n")      // "\\r"    -> "\n"
            .replace("\r\n", "\n")     // 실제 CRLF -> LF
            .replace("\r", "\n")       // 실제 CR   -> LF
    }
}
