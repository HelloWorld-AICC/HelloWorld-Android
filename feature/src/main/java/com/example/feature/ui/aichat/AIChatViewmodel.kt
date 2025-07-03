package com.example.feature.ui.aichat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.model.aichat.ChattingRoom
import com.example.core.data.network.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AIChatViewModel @Inject constructor() : ViewModel() {

    private val _chattingRooms = MutableStateFlow<List<ChattingRoom>>(emptyList())
    val chattingRooms: StateFlow<List<ChattingRoom>> = _chattingRooms

    fun getAIChattingRooms() {
        val token = RetrofitInstance.getAccessToken()
        if (token.isNotBlank()) {
            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.aiChatService.getAIChattingRooms()
                    _chattingRooms.value = response.reversed()
                    Log.d("AIChatViewModel", "채팅방 목록: ${_chattingRooms.value}")
                } catch (e: Exception) {
                    Log.e("AIChatViewModel", "채팅방 목록 로드 실패: ${e.message}", e)
                }
            }
        } else {
            Log.w("AIChatViewModel", "토큰 없음 - 채팅방 목록 요청 보류")
        }
    }
}
