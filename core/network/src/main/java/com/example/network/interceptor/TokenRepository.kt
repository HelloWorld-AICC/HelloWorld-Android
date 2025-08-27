package com.example.network.interceptor

// 인터페이스
interface TokenRepository {
    suspend fun getAccessToken(): String
    suspend fun getRefreshToken(): String
    suspend fun setAccessToken(token: String)
    suspend fun setRefreshToken(token: String)
    suspend fun clearTokens()
}