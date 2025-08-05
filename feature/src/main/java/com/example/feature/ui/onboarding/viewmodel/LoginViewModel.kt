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
                val atk = tokenList?.firstOrNull { it.types == "ATK" }?.token
                val rtk = tokenList?.firstOrNull { it.types == "RTK" }?.token

                if (!atk.isNullOrBlank()) {
                    RetrofitInstance.setAccessToken(atk)
                    Log.d("LOGIN", "ATK 설정 성공: $atk")
                    _loginSuccess.value = true
                } else {
                    Log.e("LOGIN", "ATK가 비어 있거나 없음")
                }

                // 저장 완료 후 자동 로그인 시도
                if (!rtk.isNullOrBlank()) {
                    val success = RetrofitInstance.tryAutoLogin()
                    if (success) {
                        Log.d("AUTO_LOGIN ", "자동 로그인 성공")
                        _loginSuccess.value = true
                    } else {
                        Log.w("AUTO_LOGIN ", "자동 로그인 실패")
                    }
                } else {
                    Log.w("AUTO_LOGIN ", "RTK가 없어 자동 로그인 생략")
                }

            } catch (e: Exception) {
                Log.e("LOGIN", "토큰 요청 실패", e)
            }
        }
    }
} // Added LoginViewModel for handling Google login