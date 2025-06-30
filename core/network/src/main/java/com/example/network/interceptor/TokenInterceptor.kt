package com.example.network.interceptor

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository
) : Interceptor {
    companion object {
        private const val TAG = "AuthInterceptor"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val TOKEN_TYPE = "Bearer"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 1. auth 관련 API는 토큰 없이 요청
        val isAuthApi = originalRequest.url.encodedPath.let { path ->
            // TODO 경로 수정 필요
            path.contains("/oauth/") ||
                    path.contains("/users/signup") ||
                    path.contains("/users/login") ||
                    path.contains("/auth/email/") ||
                    path.contains("/auth/password/") ||
                    path.contains("/auth/refresh")
        }

        if (isAuthApi) {
            return chain.proceed(originalRequest)
        }

        // 2. 토큰이 필요한 API 처리
        return runBlocking {
            executeWithAuth(chain, originalRequest)
        }
    }

    private suspend fun executeWithAuth(chain: Interceptor.Chain, originalRequest: okhttp3.Request): Response {
        val accessToken = tokenRepository.getAccessToken()
        Log.d(TAG, "access token : $accessToken")

        val authenticationRequest = originalRequest.newBuilder()
            .addHeader(HEADER_AUTHORIZATION, "$TOKEN_TYPE $accessToken")
            .build()

        val response = chain.proceed(authenticationRequest)

        return response
    }
}