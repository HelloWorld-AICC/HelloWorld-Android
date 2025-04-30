package com.example.helloworld.ui.chat_consultation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatConsultationViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Chat Consultation Fragment"
    }
    val text: LiveData<String> = _text
}