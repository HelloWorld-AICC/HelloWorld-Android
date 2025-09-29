package com.example.core.data.token

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.network.interceptor.TokenRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Context 확장: DataStore
private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenRepository {

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("atk")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("rtk")
    }

    override suspend fun getAccessToken(): String? {
        return context.dataStore.data.map { prefs -> prefs[KEY_ACCESS_TOKEN] }.first()
    }

    override suspend fun getRefreshToken(): String? {
        return context.dataStore.data.map { prefs -> prefs[KEY_REFRESH_TOKEN] }.first()
    }

    override suspend fun setAccessToken(token: String) {
        context.dataStore.edit { prefs -> prefs[KEY_ACCESS_TOKEN] = token }
    }

    override suspend fun setRefreshToken(token: String) {
        context.dataStore.edit { prefs -> prefs[KEY_REFRESH_TOKEN] = token }
    }

    override suspend fun clearTokens() {
        context.dataStore.edit { prefs -> prefs.clear() }
    }
}
