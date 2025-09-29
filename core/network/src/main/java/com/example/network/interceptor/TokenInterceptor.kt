package com.example.network.interceptor

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton


// TokenRepositoryImpl (DataStore 기반).
@Singleton
class TokenInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository
) : Interceptor {

    companion object {
        private const val TAG = "TokenInterceptor"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val TOKEN_TYPE = "Bearer"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 인증 불필요 API → 토큰 붙이지 않음
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

        // 토큰이 필요한 API
        return runBlocking {
            val accessToken = tokenRepository.getAccessToken()
            Log.d(TAG, "현재 AccessToken: $accessToken")

            if (!accessToken.isNullOrBlank()) {
                val authRequest = originalRequest.newBuilder()
                    .addHeader(HEADER_AUTHORIZATION, "$TOKEN_TYPE $accessToken")
                    .build()
                chain.proceed(authRequest)
            } else {
                Log.w(TAG, "토큰 없음 → 로그인 필요")
                chain.proceed(originalRequest)
            }
        }
    }
}
