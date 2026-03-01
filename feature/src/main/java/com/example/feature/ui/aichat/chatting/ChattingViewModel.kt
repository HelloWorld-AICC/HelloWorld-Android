package com.example.feature.ui.aichat.chatting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.model.aichat.AIChatLogResponse
import com.example.core.data.model.aichat.AIChatMessage
import com.example.core.data.model.aichat.AIChatSseEvent
import com.example.core.data.model.aichat.AskToAIRequest
import com.example.core.data.network.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.EOFException
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import retrofit2.HttpException

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val json = Json { ignoreUnknownKeys = true }

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

    fun loadChatLog(
        roomId: String,
        greeting: String,
        prompt: String
    ) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Request getAIChatLog($roomId)")
                val resp = RetrofitInstance.aiChatService.getAIChatLog(roomId)

                if (!resp.isSuccessful) {
                    Log.w(TAG, "getAIChatLog failed: HTTP ${resp.code()}")
                    setMessagesWithIntro(roomId, greeting, prompt, emptyList())
                    return@launch
                }

                val body: AIChatLogResponse = resp.body() ?: run {
                    Log.w(TAG, "getAIChatLog body=null")
                    setMessagesWithIntro(roomId, greeting, prompt, emptyList())
                    return@launch
                }

                val chatLogData = body.data
                Log.d(TAG, "Loaded roomId=${chatLogData.roomId}, chatLogs=${chatLogData.chatLogs.size}")

                val cleanedLogs = chatLogData.chatLogs.map { message ->
                    val base = if (message.sender.equals("user", ignoreCase = true)) {
                        message.content.removeSurrounding("\"")
                    } else {
                        message.content
                    }

                    AIChatMessage(
                        content = normalizeServerText(base),
                        sender = message.sender
                    )
                }

                setMessagesWithIntro(
                    roomId = chatLogData.roomId,
                    greeting = greeting,
                    prompt = prompt,
                    logs = cleanedLogs
                )

                if (!_chatRoomIds.value.contains(chatLogData.roomId)) {
                    _chatRoomIds.value = _chatRoomIds.value + chatLogData.roomId
                }
                _selectedChatId.value = chatLogData.roomId
            } catch (e: IOException) {
                Log.e(TAG, "Network error(getAIChatLog): ${e.message}", e)
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP error(getAIChatLog): ${e.message}", e)
            } catch (e: Exception) {
                Log.e(TAG, "Unknown error(getAIChatLog): ${e.message}", e)
            }
        }
    }

    private fun setMessagesWithIntro(
        roomId: String,
        greeting: String,
        prompt: String,
        logs: List<AIChatMessage>
    ) {
        val intro = listOf(
            AIChatMessage(content = greeting, sender = "bot"),
            AIChatMessage(content = prompt, sender = "bot")
        )
        val finalMessages = intro + logs

        _chatMessages.value = _chatMessages.value.toMutableMap().apply {
            put(roomId, finalMessages)
        }
    }

    fun sendUserMessage(chatId: String, userMessage: String) {
        val current = _chatMessages.value[chatId].orEmpty()
        val updated = current + AIChatMessage(content = userMessage, sender = "user")
        _chatMessages.value = _chatMessages.value.toMutableMap().apply { put(chatId, updated) }

        viewModelScope.launch {
            _isTyping.value = true
            var streamedText = ""
            var finalRoomId: String? = null

            Log.d(TAG, "Send user message: \"$userMessage\"")

            askToAIWithStream(
                roomId = chatId,
                message = userMessage,
                onPartialResponse = { partial ->
                    streamedText += partial
                    val currentMessages = _chatMessages.value[chatId].orEmpty()

                    val newList = if (currentMessages.lastOrNull()?.sender == "bot") {
                        currentMessages.dropLast(1) + AIChatMessage(streamedText, "bot")
                    } else {
                        currentMessages + AIChatMessage(streamedText, "bot")
                    }

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
                    } else {
                        _selectedChatId.value = finalRoomId
                    }

                    Log.d(TAG, "Streaming complete: finalRoomId=$finalRoomId")
                }
            )
        }
    }

    private suspend fun askToAIWithStream(
        roomId: String,
        message: String,
        onPartialResponse: (String) -> Unit,
        onComplete: (String?) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Request askToAI(roomId=$roomId, message=$message)")
            val requestBody = AskToAIRequest(query = message)
            val resp = RetrofitInstance.aiChatService.askToAI(roomId, requestBody)

            if (!resp.isSuccessful) {
                Log.w(TAG, "askToAI failed: HTTP ${resp.code()}")
                withContext(Dispatchers.Main) { onComplete(null) }
                return@withContext
            }

            val body = resp.body()
            val source = body?.source()
            if (source == null) {
                Log.w(TAG, "askToAI body=null")
                withContext(Dispatchers.Main) { onComplete(null) }
                return@withContext
            }

            var roomIdFromStream: String? = null

            try {
                while (!source.exhausted()) {
                    val line = try {
                        source.readUtf8Line()
                    } catch (e: EOFException) {
                        null
                    } ?: break

                    Log.d(TAG, "SSE received: $line")

                    if (line.isBlank() || !line.startsWith("data:")) continue

                    val payload = line.removePrefix("data:").trim()
                    if (payload.isEmpty()) continue

                    if (payload == "[DONE]" || payload == "[COMPLETE]") break

                    if (payload.startsWith("Room ID:")) {
                        roomIdFromStream = payload.removePrefix("Room ID:").trim()
                        continue
                    }

                    val event = runCatching {
                        json.decodeFromString<AIChatSseEvent>(payload)
                    }.getOrNull()

                    if (event == null) {
                        withContext(Dispatchers.Main) {
                            onPartialResponse(normalizeServerText(payload))
                        }
                        continue
                    }

                    when (event.type) {
                        "metadata" -> Unit
                        "done" -> break
                        "content" -> {
                            val content = event.content ?: continue
                            withContext(Dispatchers.Main) {
                                onPartialResponse(normalizeServerText(content))
                            }
                        }
                    }
                }
            } finally {
                body.close()
            }

            withContext(Dispatchers.Main) {
                onComplete(roomIdFromStream)
            }
        } catch (e: CancellationException) {
            Log.w(TAG, "Streaming cancelled")
            withContext(Dispatchers.Main) { onComplete(null) }
        } catch (e: Exception) {
            Log.e(TAG, "askToAIWithStream failed", e)
            withContext(Dispatchers.Main) { onComplete(null) }
        }
    }

    fun summarizeMessage() {
        val roomId = _selectedChatId.value ?: return
        viewModelScope.launch {
            try {
                Log.d(TAG, "Request summary: roomId=$roomId")
                val body = RetrofitInstance.aiChatService.summaryAIChat(roomId).body() ?: return@launch
                val result = body.use { it.string() }
                Log.d(TAG, "Summary result: $result")

                if (result.trim().equals("complete", ignoreCase = true)) {
                    _summaryCompleted.tryEmit(Unit)
                } else {
                    Log.w(TAG, "Unexpected summary response: $result")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Summary request failed", e)
            }
        }
    }

    companion object {
        private const val TAG = "ChatViewModel"
    }

    private fun normalizeServerText(raw: String): String {
        return raw
            .replace("\\r\\n", "\n")
            .replace("\\n", "\n")
            .replace("\\r", "\n")
            .replace("\r\n", "\n")
            .replace("\r", "\n")
    }
}
