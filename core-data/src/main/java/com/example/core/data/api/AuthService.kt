// Retrofit 인터페이스 정의

package com.example.core.data.api

import com.example.core.data.model.TokenResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthService {
    @GET("api/v1/google/login")
    suspend fun getToken(@Query("token") idToken: String): TokenResponse
}