package com.example.network.common

import com.example.model.common.LanguageRequest

interface LanguageDataSource {
    suspend fun setLanguage(
        request: LanguageRequest
    ): Result<String>
}