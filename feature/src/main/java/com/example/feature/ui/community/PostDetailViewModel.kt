package com.example.feature.ui.community

import androidx.lifecycle.ViewModel
import com.example.core.ui.component.DialogData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(

) : ViewModel() {

    private val _commentText = MutableStateFlow<String>("")
    val commentText: StateFlow<String> = _commentText

    private val _dialogData = MutableStateFlow<DialogData?>(null)
    val dialogData: StateFlow<DialogData?> = _dialogData

    fun updateComment(comment: String) {
        _commentText.value = comment
    }

    fun updateDialogData(data: DialogData? = null) {
        _dialogData.value = data
    }
}