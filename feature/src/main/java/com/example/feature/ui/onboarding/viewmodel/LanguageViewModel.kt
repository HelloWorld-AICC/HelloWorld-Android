package com.example.feature.ui.onboarding.viewmodel

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.common.LanguageRepository
import com.example.model.common.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Result // Kotlin 표준 Result<T>

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val languageRepository: LanguageRepository
) : ViewModel() {

    private val _editingLanguage = MutableStateFlow<Language?>(null)
    val editingLanguage: StateFlow<Language?> = _editingLanguage.asStateFlow()

    fun updateLanguage(language: Language) {
        _editingLanguage.value = language
    }

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            Log.d(TAG, "언어 설정 API 요청 시작: ${language.displayName} (${language.code})")

            try {
                // 서버 API 호출
                val result: Result<String> = languageRepository.submitLanguage(language)

                result.onSuccess {
                    Log.d(TAG, "서버 저장 성공: $it")
                    saveToDatastore(language)   // Datastore 저장
                    applyLanguage(language)     // 앱 언어 적용
                }.onFailure {
                    Log.e(TAG, "서버 저장 실패: ${it.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "예외 발생: ${e.message}", e)
            }
        }
    }

    private suspend fun saveToDatastore(language: Language) {
        try {
            languageRepository.setLanguage(language)
            Log.d(TAG, "로컬 저장 성공 (Datastore): ${language.displayName}")
        } catch (e: Exception) {
            Log.e(TAG, "로컬 저장 실패: ${e.message}", e)
        }
    }

    private fun applyLanguage(language: Language) {
        try {
            val localeList = LocaleListCompat.forLanguageTags(language.localeCode)
            AppCompatDelegate.setApplicationLocales(localeList)
            Log.d(TAG, "앱 언어 적용 성공: ${language.displayName} (${language.localeCode})")
        } catch (e: Exception) {
            Log.e(TAG, "앱 언어 적용 실패: ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "LanguageViewModel"
    }
}
