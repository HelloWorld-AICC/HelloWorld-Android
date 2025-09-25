package com.example.feature.ui.onboarding.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.model.LoginEmailRequest
import com.example.core.data.model.LoginTokenItem
import com.example.core.data.model.TokenResponse
import com.example.core.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    /**
     * Google 로그인 처리
     * @param email 구글 계정 이메일
     * @param accessToken 구글 accessToken (authCode 교환 후 얻음)
     */
    fun handleGoogleLogin(email: String?, accessToken: String?) {
        if (email.isNullOrBlank()) {
            Log.e("LOGIN", "이메일이 null이거나 비어 있음")
            return
        }

        viewModelScope.launch {
            try {
                // 1) 이메일 로그인 먼저 시도
                val emailResponse = RetrofitInstance.authService.loginWithEmail(LoginEmailRequest(email))

                if (emailResponse.isSuccess) {
                    Log.d("LOGIN", "이메일 로그인 성공")
                    saveTokens(emailResponse.result?.tokenList)
                    _loginSuccess.value = true
                } else {
                    Log.w("LOGIN", "이메일 로그인 실패 → 구글 로그인 시도")

                    // 2) 이메일 로그인 실패 시 신규 가입 (accessToken 필요)
                    if (!accessToken.isNullOrBlank()) {
                        val googleResponse = RetrofitInstance.authService.getToken(accessToken)
                        if (googleResponse.isSuccess) {
                            Log.d("LOGIN", "구글 로그인 성공 (신규 가입)")
                            saveTokens(googleResponse.result?.tokenList)
                            _loginSuccess.value = true
                        } else {
                            Log.e("LOGIN", "구글 로그인 API 실패")
                        }
                    } else {
                        Log.e("LOGIN", "accessToken이 null이라 구글 로그인 불가")
                    }
                }
            } catch (e: Exception) {
                Log.e("LOGIN", "로그인 과정에서 오류", e)
            }
        }
    }

    private fun saveTokens(tokenList: List<LoginTokenItem>?) {
        val atk = tokenList?.firstOrNull { it.types.equals("ATK", true) }?.token
        val rtk = tokenList?.firstOrNull { it.types.equals("RTK", true) }?.token

        if (!atk.isNullOrBlank()) {
            RetrofitInstance.setAccessToken(atk)
            Log.d("LOGIN", "ATK 설정 성공: $atk")
        } else {
            Log.e("LOGIN", "ATK가 비어 있거나 없음")
        }

        if (!rtk.isNullOrBlank()) {
            RetrofitInstance.setRefreshToken(rtk)
            Log.d("LOGIN", "RTK 설정 성공: $rtk")
        } else {
            Log.w("LOGIN", "RTK가 비어 있음")
        }
    }
}
