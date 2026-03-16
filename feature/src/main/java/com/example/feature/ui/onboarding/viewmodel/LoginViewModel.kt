package com.example.feature.ui.onboarding.viewmodel

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.common.LanguageRepository
import com.example.core.data.model.LoginTokenItem
import com.example.core.data.mypage.MyPageRepository
import com.example.core.data.network.RetrofitInstance
import com.example.model.common.Language
import com.example.network.interceptor.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val myPageRepository: MyPageRepository,
    private val languageRepository: LanguageRepository
) : ViewModel() {

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _isExistUser = MutableStateFlow(false)
    val isExistUser: StateFlow<Boolean> = _isExistUser

    fun handleGoogleLogin(email: String?, authCode: String?, accessToken: String?, idToken: String?) {
        viewModelScope.launch {
            try {
                if (!idToken.isNullOrBlank()) {
                    val googleResponse = RetrofitInstance.authService.getToken(idToken)

                    if (googleResponse.isSuccess) {
                        saveTokens(googleResponse.result.tokenList)
                        syncLanguageFromServer()

                        _loginSuccess.value = true
                        _isExistUser.value = googleResponse.result.isExist
                    } else {
                        Log.e("LOGIN", "Google login API failed")
                    }
                } else {
                    Log.e("LOGIN", "idToken is null, cannot login")
                }
            } catch (e: Exception) {
                Log.e("LOGIN", "login failed", e)
            }
        }
    }

    private suspend fun saveTokens(tokenList: List<LoginTokenItem>?) {
        val atk = tokenList?.firstOrNull { it.types.equals("ATK", true) }?.token
        val rtk = tokenList?.firstOrNull { it.types.equals("RTK", true) }?.token

        if (!atk.isNullOrBlank()) {
            RetrofitInstance.setAccessToken(atk)
            tokenRepository.setAccessToken(atk)
            Log.d("LOGIN", "ATK saved")
        } else {
            Log.e("LOGIN", "ATK is empty")
        }

        if (!rtk.isNullOrBlank()) {
            RetrofitInstance.setRefreshToken(rtk)
            tokenRepository.setRefreshToken(rtk)
            Log.d("LOGIN", "RTK saved")
        } else {
            Log.w("LOGIN", "RTK is empty")
        }
    }

    private suspend fun syncLanguageFromServer() {
        myPageRepository.getMyLanguage()
            .onSuccess { response ->
                val languageName = response.language.firstOrNull()
                val language = Language.entries.firstOrNull {
                    it.displayName.equals(languageName, ignoreCase = true) ||
                        it.name.equals(languageName, ignoreCase = true) ||
                        it.localeCode.equals(languageName, ignoreCase = true)
                }

                if (language != null) {
                    languageRepository.setLanguage(language)
                    val localeList = LocaleListCompat.forLanguageTags(language.localeCode)
                    AppCompatDelegate.setApplicationLocales(localeList)
                    Log.d("LOGIN", "Language synced: ${language.displayName}")
                } else {
                    Log.w("LOGIN", "Language mapping failed: $languageName")
                }
            }
            .onFailure { e ->
                Log.w("LOGIN", "Language sync failed", e)
            }
    }
}
