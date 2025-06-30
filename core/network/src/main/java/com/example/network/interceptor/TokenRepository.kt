package com.example.network.interceptor

interface TokenRepository {
    suspend fun getAccessToken(): String
}