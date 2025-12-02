package com.example.feature.ui.onboarding.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.model.LoginEmailRequest
import com.example.core.data.model.LoginTokenItem
import com.example.core.data.network.RetrofitInstance
import com.example.network.interceptor.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _isExistUser = MutableStateFlow(false)
    val isExistUser: StateFlow<Boolean> = _isExistUser

    fun handleGoogleLogin(email: String?, accessToken: String?) {
        if (email.isNullOrBlank()) {
            Log.e("LOGIN", "이메일이 null이거나 비어 있음")
            return
        }

        viewModelScope.launch {
            try {
                val emailResponse = RetrofitInstance.authService.loginWithEmail(LoginEmailRequest(email))

                if (emailResponse.isSuccess) { // 기존 회원
                    Log.d("LOGIN", "이메일 로그인 성공")
                    saveTokens(emailResponse.result?.tokenList)
                    _loginSuccess.value = true
                    _isExistUser.value = true
                } else {
                    Log.w("LOGIN", "이메일 로그인 실패 → 구글 로그인 시도")

                    if (!accessToken.isNullOrBlank()) {
                        val googleResponse = RetrofitInstance.authService.getToken(accessToken)
                        if (googleResponse.isSuccess) {
                            Log.d("LOGIN", "구글 로그인 성공 (신규 가입)")
                            saveTokens(googleResponse.result?.tokenList)
                            _loginSuccess.value = true
                            _isExistUser.value = false
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

        viewModelScope.launch {
            if (!atk.isNullOrBlank()) {
                // SharedPreferences 저장
                RetrofitInstance.setAccessToken(atk)
                Log.d("LOGIN", "ATK 설정 성공 (Prefs): $atk")

                // DataStore 저장
                tokenRepository.setAccessToken(atk)
                Log.d("LOGIN", "ATK 설정 성공 (DataStore): $atk")
            } else {
                Log.e("LOGIN", "ATK가 비어 있거나 없음")
            }

            if (!rtk.isNullOrBlank()) {
                RetrofitInstance.setRefreshToken(rtk)
                Log.d("LOGIN", "RTK 설정 성공 (Prefs): $rtk")

                tokenRepository.setRefreshToken(rtk)
                Log.d("LOGIN", "RTK 설정 성공 (DataStore): $rtk")
            } else {
                Log.w("LOGIN", "RTK가 비어 있음")
            }
        }
    }
}
