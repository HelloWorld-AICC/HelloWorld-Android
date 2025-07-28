package com.example.datastore

import com.example.model.common.Language

interface LanguageDataStore {
    suspend fun getLanguage(): Language
    suspend fun setLanguage(language: Long)
}