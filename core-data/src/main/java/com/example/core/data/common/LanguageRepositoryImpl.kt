package com.example.core.data.common

import com.example.datastore.LanguageDataStore
import com.example.model.common.Language
import com.example.model.common.LanguageRequest
import com.example.network.common.LanguageDataSource
import javax.inject.Inject

class LanguageRepositoryImpl @Inject constructor(
    private val languageDataStore: LanguageDataStore,
    private val languageDataSource: LanguageDataSource,
) : LanguageRepository {
    override suspend fun getLanguage(): Language {
        return languageDataStore.getLanguage()
    }

    override suspend fun setLanguage(language: Language) {
        return languageDataStore.setLanguage(language.code)
    }

    override suspend fun submitLanguage(language: Language): Result<String> {
        return languageDataSource.setLanguage(LanguageRequest(language.code))
    }
}