package com.example.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.example.model.common.Language
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LanguageDataStoreImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : LanguageDataStore {

    companion object {
        private val LANGUAGE_KEY = longPreferencesKey("selected_language")
    }

    override suspend fun getLanguage(): Language {
        return dataStore.data.map { preferences ->
            Language.fromCode(preferences[LANGUAGE_KEY] ?: 1L) ?: Language.ENGLISH
        }.first()
    }

    override suspend fun setLanguage(language: Long) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }
}