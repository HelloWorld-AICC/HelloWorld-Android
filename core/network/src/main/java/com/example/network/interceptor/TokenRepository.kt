package com.example.network.interceptor

// DataStore 기반
interface TokenRepository {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun setAccessToken(token: String)
    suspend fun setRefreshToken(token: String)
    suspend fun clearTokens()
}