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

        // 1. 인증 불필요 API는 토큰 없이 요청
        val isAuthApi = originalRequest.url.encodedPath.let { path ->
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

        // 2. 토큰이 필요한 API → 헤더에 ATK 추가
        return runBlocking {
            val accessToken = tokenRepository.getAccessToken()
            Log.d(TAG, "현재 AccessToken: $accessToken")

            val authRequest = originalRequest.newBuilder()
                .addHeader(HEADER_AUTHORIZATION, "$TOKEN_TYPE $accessToken")
                .build()

            chain.proceed(authRequest)
        }
    }
}
