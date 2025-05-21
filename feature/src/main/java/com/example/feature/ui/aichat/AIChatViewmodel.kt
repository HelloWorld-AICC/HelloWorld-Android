package com.example.feature.ui.aichat

import androidx.lifecycle.ViewModel
import com.example.core.data.chatInfo.ChatInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AIChatViewModel : ViewModel() {

    private val _conversations = MutableStateFlow<List<ChatInfo>>(emptyList())
    val conversations: StateFlow<List<ChatInfo>> = _conversations.asStateFlow()

    init {
        // 실제 앱에선 repository로부터 불러오게 됨
        _conversations.value = listOf(
            ChatInfo(1, "직장 내 고충", "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(2, "기타", "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(3, "직장 내 고충", "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(4, "기타", "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(5, "직장 내 고충", "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(6, "직장 내 고충", "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(7, "기타", "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(8, "직장 내 고충", "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(9, "기타", "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(10, "직장 내 고충", "임금 체불과 직장 내 괴롭힘"),
        )
    }

    fun startNewChat() {
        // 새 채팅 시작 로직
    }
}