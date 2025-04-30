package com.example.helloworld.ui.resume_writing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResumeWritingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Resume Writing Fragment"
    }
    val text: LiveData<String> = _text
}