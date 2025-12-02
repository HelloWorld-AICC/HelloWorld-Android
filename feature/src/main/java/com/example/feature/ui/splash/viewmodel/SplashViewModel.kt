package com.example.feature.ui.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.network.RetrofitInstance
import com.example.network.interceptor.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenRepository: TokenRepository
) : ViewModel() {


    private val _isAutoLoginSuccess = MutableStateFlow<Boolean?>(null)
    val isAutoLoginSuccess = _isAutoLoginSuccess.asStateFlow()

    fun checkAutoLogin() {
        viewModelScope.launch {
            val rtk = try {
                RetrofitInstance.getRefreshToken()
            } catch (e: Exception) {
                Log.e("AUTO_LOGIN", "RTK 조회 실패", e)
                null
            }

            if (rtk.isNullOrEmpty()) {
                Log.d("AUTO_LOGIN", "RTK 없음 → 자동 로그인 시도 안 함")
                _isAutoLoginSuccess.value = false
                return@launch
            }

            try {
                Log.d("AUTO_LOGIN", "RTK 있음 → 재발급 시도")
                val response = RetrofitInstance.authService.reissueToken(rtk)
                Log.d("AUTO_LOGIN", "재발급 성공 응답: $response")

                if (response.isSuccess && response.result?.tokenList != null) {
                    val atk = response.result.tokenList
                        .find { it.types.equals("ATK", ignoreCase = true) }
                        ?.token.orEmpty()
                    val rtk = response.result.tokenList
                        .find { it.types.equals("RTK", ignoreCase = true) }
                        ?.token.orEmpty()

                    if (atk.isNotEmpty() &&  rtk.isNotEmpty()) {
                        // SharedPreferences 저장
                        RetrofitInstance.setAccessToken(atk)
                        Log.d("AUTO_LOGIN", "RetrofitInstance에 ATK 저장 완료: $atk")

                        RetrofitInstance.setRefreshToken(rtk)
                        Log.d("AUTO_LOGIN", "RetrofitInstance에 RTK 저장 완료: $rtk")


                        // DataStore 저장
                        tokenRepository.setAccessToken(atk)
                        Log.d("AUTO_LOGIN", "TokenRepository(DataStore)에 ATK 저장 완료: $atk")
                        Log.d("AUTO_LOGIN", "토큰 재발급 성공 → ATK 갱신 완료")

                        tokenRepository.setRefreshToken(rtk)
                        Log.d("AUTO_LOGIN", "TokenRepository(DataStore)에 RTK 저장 완료: $atk")
                        Log.d("AUTO_LOGIN", "토큰 재발급 성공 → RTK 갱신 완료")

                        _isAutoLoginSuccess.value = true
                    } else {
                        Log.w("AUTO_LOGIN", "재발급 응답에 ATK or RTK 없음")
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
