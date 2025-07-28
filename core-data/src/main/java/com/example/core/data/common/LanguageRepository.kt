package com.example.core.data.common

import com.example.model.common.Language

interface LanguageRepository {
    suspend fun getLanguage(): Language                             // Preference
    suspend fun setLanguage(language: Language)                     // Preference

    suspend fun submitLanguage(language: Language): Result<String>    // Rest Api
}