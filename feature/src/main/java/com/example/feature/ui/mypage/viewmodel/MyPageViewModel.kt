
package com.example.feature.ui.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.ui.component.DialogData
import com.example.domain.UserInfoUseCase
import com.example.model.common.Result
import com.example.model.mypage.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userInfoUseCase: UserInfoUseCase,
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
                userInfoUseCase().collect {
                    when (it) {
                        is Result.Loading -> _uiState.value = MyPageUiState.Loading
                        is Result.Success<UserInfo> -> _uiState.value = MyPageUiState.Success(it.data)
                        is Result.Error -> _uiState.value = MyPageUiState.Error(it.exception.message.toString())
                    }
                }
            } catch (e: Exception) {
                _uiState.value = MyPageUiState.Error("${e.message}")
            }
        }
    }

    fun logout(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            // TODO logout (token 제거)
            onResult(true)
        }
    }
}

sealed interface MyPageUiState {
    data object Loading: MyPageUiState
    data class Success(val userInfo: UserInfo): MyPageUiState
    data class Error(val msg: String): MyPageUiState
}