
package com.example.feature.ui.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.mypage.MyPageRepository
import com.example.core.ui.component.DialogData
import com.example.model.mypage.MyPageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository
) : ViewModel() {

    private val _dialogData = MutableStateFlow<DialogData?>(null)
    val dialogData: StateFlow<DialogData?> = _dialogData.asStateFlow()

    // 유저 정보
    private val _uiState = MutableStateFlow<MyPageUiState>(MyPageUiState.Loading)
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    fun updateDialogData(data: DialogData? = null) {
        _dialogData.value = data
    }

    init {
        getUserInfo()
    }

    // 유저 정보 가져오기
    fun getUserInfo() {
        viewModelScope.launch {
            _uiState.value = MyPageUiState.Loading
            try {
                val userInfo = myPageRepository.getMyPage().getOrThrow()
                _uiState.value = MyPageUiState.Success(userInfo)
            } catch (e: Exception) {
                _uiState.value = MyPageUiState.Error("${e.message}")
            }
        }
    }
}

sealed interface MyPageUiState {
    data object Loading: MyPageUiState
    data class Success(val userInfo: MyPageResponse): MyPageUiState
    data class Error(val msg: String): MyPageUiState
}