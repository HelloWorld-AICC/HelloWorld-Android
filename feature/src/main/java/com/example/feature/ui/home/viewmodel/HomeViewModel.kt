package com.example.feature.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.AuthUseCase
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
class HomeViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val userInfoUseCase: UserInfoUseCase,
) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    fun fetchUserInfoIfTokenExists() {
        Log.d("HomeViewModel", "fetchUserInfoIfTokenExists() called")

        viewModelScope.launch {
            val hasToken = authUseCase.hasToken()
            if (!hasToken) {   // 토큰이 없을 때만 return
                Log.w("HomeViewModel", "토큰 없음 → 사용자 정보 요청 안 함")
                return@launch
            }

            Log.d("HomeViewModel", "userInfoUseCase() 호출중...")
            userInfoUseCase().collect {
                when (it) {
                    is Result.Success -> {
                        Log.d("HomeViewModel", "유저 정보 성공: ${it.data}")
                        _userInfo.value = it.data
                    }

                    is Result.Error -> {
                        Log.e("HomeViewModel", "유저 정보 실패: ${it.exception}")
                    }

                    is Result.Loading -> {
                        Log.d("HomeViewModel", "유저 정보 로딩중...")
                    }
                }
            }
        }
    }
}