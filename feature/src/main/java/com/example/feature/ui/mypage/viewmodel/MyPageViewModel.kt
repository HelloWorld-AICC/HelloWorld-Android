package com.example.feature.ui.mypage.viewmodel

import androidx.lifecycle.ViewModel
import com.example.core.ui.component.DialogData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(

) : ViewModel() {

    private val _dialogData = MutableStateFlow<DialogData?>(null)
    val dialogData: StateFlow<DialogData?> = _dialogData

    fun updateDialogData(data: DialogData? = null) {
        _dialogData.value = data
    }
}