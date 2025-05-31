// Retrofit 인터페이스 정의

package com.example.core.data.api

import com.example.core.data.model.TokenResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthService {
    @GET("api/v1/google/code")
    suspend fun getToken(@Query("code") authCode: String): TokenResponse
}