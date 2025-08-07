package com.example.feature.ui.onboarding.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.common.LanguageRepository
import com.example.model.common.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Result // Kotlin 표준 Result<T>

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val languageRepository: LanguageRepository
) : ViewModel() {

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            Log.d(TAG, "언어 설정 API 요청 시작: ${language.displayName} (${language.code})")

            try {
                // 서버 API 호출
                val result: Result<String> = languageRepository.submitLanguage(language)

                result.onSuccess {
                    Log.d(TAG, "서버 저장 성공: $it")
                    saveToDatastore(language)   // Datastore 저장
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

    companion object {
        private const val TAG = "LanguageViewModel"
    }
}
