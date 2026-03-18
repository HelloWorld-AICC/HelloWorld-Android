package com.example.feature.ui.splash

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.common.LanguageRepository
import com.example.core.data.mypage.MyPageRepository
import com.example.core.data.network.RetrofitInstance
import com.example.model.common.Language
import com.example.network.interceptor.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val myPageRepository: MyPageRepository,
    private val languageRepository: LanguageRepository
) : ViewModel() {

    private val _isAutoLoginSuccess = MutableStateFlow<Boolean?>(null)
    val isAutoLoginSuccess = _isAutoLoginSuccess.asStateFlow()

    fun checkAutoLogin() {
        viewModelScope.launch {
            val savedRefreshToken = try {
                RetrofitInstance.getRefreshToken()
            } catch (e: Exception) {
                Log.e("AUTO_LOGIN", "Failed to read refresh token", e)
                null
            }

            if (savedRefreshToken.isNullOrEmpty()) {
                Log.d("AUTO_LOGIN", "No refresh token. Auto login failed")
                _isAutoLoginSuccess.value = false
                return@launch
            }

            try {
                val response = RetrofitInstance.authService.reissueToken(savedRefreshToken)

                if (response.isSuccess && response.result?.tokenList != null) {
                    val accessToken = response.result.tokenList
                        .find { it.types.equals("ATK", ignoreCase = true) }
                        ?.token.orEmpty()
                    val refreshToken = response.result.tokenList
                        .find { it.types.equals("RTK", ignoreCase = true) }
                        ?.token.orEmpty()

                    if (accessToken.isNotEmpty() && refreshToken.isNotEmpty()) {
                        RetrofitInstance.setAccessToken(accessToken)
                        RetrofitInstance.setRefreshToken(refreshToken)

                        tokenRepository.setAccessToken(accessToken)
                        tokenRepository.setRefreshToken(refreshToken)

                        syncLanguageFromServer()
                        _isAutoLoginSuccess.value = true
                    } else {
                        Log.w("AUTO_LOGIN", "Token reissue response missing ATK or RTK")
                        _isAutoLoginSuccess.value = false
                    }
                } else {
                    Log.w("AUTO_LOGIN", "Token reissue failed")
                    _isAutoLoginSuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("AUTO_LOGIN", "Auto login exception", e)
                _isAutoLoginSuccess.value = false
            }
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
                    Log.d("AUTO_LOGIN", "Language synced: ${language.displayName}")
                } else {
                    Log.w("AUTO_LOGIN", "Language mapping failed: $languageName")
                }
            }
            .onFailure { e ->
                Log.w("AUTO_LOGIN", "Language sync failed", e)
            }
    }
}
