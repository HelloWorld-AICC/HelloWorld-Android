package com.example.domain

import com.example.network.interceptor.TokenRepository
import javax.inject.Inject

// 토큰이 있는지 확인하는 usecase
class AuthUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    suspend fun hasToken(): Boolean {
        val atk = tokenRepository.getAccessToken()
        return !atk.isNullOrBlank()
    }
}
