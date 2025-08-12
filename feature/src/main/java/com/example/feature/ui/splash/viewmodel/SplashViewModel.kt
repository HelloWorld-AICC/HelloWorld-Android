package com.example.feature.ui.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _isAutoLoginSuccess = MutableStateFlow<Boolean?>(null)
    val isAutoLoginSuccess = _isAutoLoginSuccess.asStateFlow()

    fun checkAutoLogin() {
        viewModelScope.launch {
            val rtk = RetrofitInstance.getRefreshToken()

            // RTK 없으면 바로 온보딩 분기
            if (rtk.isNullOrEmpty()) {
                Log.d("AUTO_LOGIN", "RTK 없음 → 자동 로그인 시도 안 함")
                _isAutoLoginSuccess.value = false
                return@launch
            }

            try {
                Log.d("AUTO_LOGIN", "RTK 있음 → 재발급 시도")
                val response = RetrofitInstance.authService.reissueToken(rtk)

                if (response.isSuccess) {
                    val atk = response.result.tokenList
                        .find { it.types.equals("ATK", ignoreCase = true) }
                        ?.token.orEmpty()

                    if (atk.isNotEmpty()) {
                        RetrofitInstance.setAccessToken(atk)
                        Log.d("AUTO_LOGIN", "토큰 재발급 성공 → ATK 갱신 완료")
                        _isAutoLoginSuccess.value = true
                    } else {
                        Log.w("AUTO_LOGIN", "재발급 응답에 ATK 없음")
                        _isAutoLoginSuccess.value = false
                    }
                } else {
                    Log.w("AUTO_LOGIN", "토큰 재발급 실패")
                    _isAutoLoginSuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("AUTO_LOGIN", "자동 로그인 중 오류", e)
                _isAutoLoginSuccess.value = false
            }
        }
    }
}
