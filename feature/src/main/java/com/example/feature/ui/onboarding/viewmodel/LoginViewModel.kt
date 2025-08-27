package com.example.feature.ui.onboarding.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.core.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    fun handleGoogleLogin(idToken: String?) {
        if (idToken == null) {
            Log.e("LOGIN", "idToken이 null입니다")
            return
        }

        Log.d("LOGIN", "idToken: $idToken")

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.authService.getToken(idToken)

                val tokenList = response.result?.tokenList
                val atk = tokenList?.firstOrNull { it.types.equals("ATK", true) }?.token
                val rtk = tokenList?.firstOrNull { it.types.equals("RTK", true) }?.token

                // ATK 없을 경우 RetrofitInstance에 설정
                if (!atk.isNullOrBlank()) {
                    RetrofitInstance.setAccessToken(atk)
                    Log.d("LOGIN", "ATK 설정 성공: $atk")
                } else {
                    Log.e("LOGIN", "ATK가 비어 있거나 없음")
                }
                
                // RTK 없을 경우 RetrofitInstance에 설정
                if (!rtk.isNullOrBlank()) {
                    RetrofitInstance.setRefreshToken(rtk)
                    Log.d("LOGIN", "RTK 설정 성공: $rtk")
                } else {
                    Log.w("LOGIN", "RTK가 비어 있음")
                }

                // ATK/RTK 저장 후 자동 로그인 시도
                if (!rtk.isNullOrBlank()) {
                    // 자동 로그인 성공 유무 확인
                    val success = RetrofitInstance.tryAutoLogin()
                    
                    // 성공일 경우
                    if (success) {
                        Log.d("AUTO_LOGIN", "자동 로그인 성공")
                        _loginSuccess.value = true
                    }
                    
                    // 실패일 경우
                    else {
                        Log.w("AUTO_LOGIN", "자동 로그인 실패")
                    }
                }

            } catch (e: Exception) {
                Log.e("LOGIN", "토큰 요청 실패", e)
            }
        }
    }
}