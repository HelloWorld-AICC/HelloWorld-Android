package com.example.helloworld.ui.consultation_center

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConsultationCenterViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Consultation Center Fragment"
    }
    val text: LiveData<String> = _text
}