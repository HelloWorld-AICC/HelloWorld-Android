package com.example.feature.ui.aichat

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class AIChatViewModel : ViewModel() {

    private val _conversations = MutableStateFlow<List<ChatInfo>>(emptyList())
    val conversations: StateFlow<List<ChatInfo>> = _conversations.asStateFlow()

    init {
        // 실제 앱에선 repository로부터 불러오게 됨
        _conversations.value = listOf(
            ChatInfo(1, LocalDate.parse("2025-04-01"), "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(2, LocalDate.parse("2025-04-02"), "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(3, LocalDate.parse("2025-04-03"), "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(4, LocalDate.parse("2025-04-04"), "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(5, LocalDate.parse("2025-04-05"), "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(6, LocalDate.parse("2025-04-06"), "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(7, LocalDate.parse("2025-04-07"), "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(8, LocalDate.parse("2025-04-08"), "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(9, LocalDate.parse("2025-04-09"), "임금 체불과 직장 내 괴롭힘"),
            ChatInfo(10, LocalDate.parse("2025-04-10"), "임금 체불과 직장 내 괴롭힘")
        )
    }

    fun startNewChat() {

    }
}