
package com.example.feature.ui.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.model.MyPageResponse
import com.example.core.data.network.RetrofitInstance
import com.example.core.ui.component.DialogData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(

) : ViewModel() {

    private val _dialogData = MutableStateFlow<DialogData?>(null)
    val dialogData: StateFlow<DialogData?> = _dialogData

    // 유저 정보
    private val _userInfo = MutableStateFlow<MyPageResponse?>(null)
    val userInfo: StateFlow<MyPageResponse?> = _userInfo

    fun updateDialogData(data: DialogData? = null) {
        _dialogData.value = data
    }


    // 헤더에 토큰 넣기 위함
    fun fetchUserInfoIfTokenExists() {
        val token = RetrofitInstance.getAccessToken()
        if (token.isNotBlank()) {
            fetchUserInfo()
        } else {
            Log.w("MyPageViewModel", "토큰 없음 - 유저 정보 요청 보류")
        }
    }

    // 유저 정보 가져오기
    fun fetchUserInfo() {
        viewModelScope.launch {
            try {
                val user = RetrofitInstance.userService.getMyPage()
                Log.d("MyPageViewModel", "userService 호출 직후 accessToken: ${RetrofitInstance.getAccessToken()}")
                _userInfo.value = user
                Log.d("MyPageViewModel", "유저 정보 가져옴: ${user.result}")
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "유저 정보 API 실패: ${e.message}")
            }
        }
    }
}
