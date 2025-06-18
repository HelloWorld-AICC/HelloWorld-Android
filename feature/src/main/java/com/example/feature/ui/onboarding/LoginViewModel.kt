package com.example.feature.ui.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.core.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    fun handleGoogleLogin(idToken: String?, navController: NavController) {
        if (idToken == null) {
            Log.e("LOGIN", "idToken이 null입니다")
            return
        }

        Log.d("LOGIN", "idToken: $idToken")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.authService.getToken(idToken)

                val tokenList = response.result?.tokenList
                val atk = tokenList?.firstOrNull { it.types == "ATK" }?.token
                val rtk = tokenList?.firstOrNull { it.types == "RTK" }?.token

                if (!atk.isNullOrBlank()) {
                    RetrofitInstance.setAccessToken(atk)
                    Log.d("LOGIN", "ATK 설정 성공: $atk")

                    withContext(Dispatchers.Main) {
                        navController.navigate("홈") {
                            popUpTo("Login") { inclusive = true }
                        }
                    }
                } else {
                    Log.e("LOGIN", "ATK가 비어 있거나 없음")
                }
            } catch (e: Exception) {
                Log.e("LOGIN", "토큰 요청 실패", e)
            }
        }
    }
} // Added LoginViewModel for handling Google login
