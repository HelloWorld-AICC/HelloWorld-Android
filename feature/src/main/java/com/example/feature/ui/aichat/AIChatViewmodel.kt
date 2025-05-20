package com.example.feature.ui.aichat

import androidx.lifecycle.ViewModel
import com.example.core.data.chatInfo.ChatInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AIChatViewModel : ViewModel() {

    private val _selectedChat = MutableStateFlow<ChatInfo?>(null)
    val selectedChat: StateFlow<ChatInfo?> = _selectedChat.asStateFlow()

    fun selectChat(chat: ChatInfo?) {
        _selectedChat.value = chat
        println(chat.toString())
    }
}