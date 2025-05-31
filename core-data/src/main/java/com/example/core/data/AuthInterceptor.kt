package com.example.core.data;

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: () -> String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = tokenProvider()
        val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        return chain.proceed(request)
    }
}
