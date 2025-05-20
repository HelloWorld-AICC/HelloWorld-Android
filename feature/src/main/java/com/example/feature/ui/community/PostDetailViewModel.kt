package com.example.feature.ui.community

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(

) : ViewModel() {

    private val _commentText = MutableStateFlow<String>("")
    val commentText: StateFlow<String> = _commentText

    fun updateComment(comment: String) {
        _commentText.value = comment
    }
}