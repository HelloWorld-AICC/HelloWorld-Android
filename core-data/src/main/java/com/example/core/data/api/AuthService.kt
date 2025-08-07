// Retrofit 인터페이스 정의

package com.example.core.data.api

import com.example.core.data.model.LoginTokenResponse
import com.example.core.data.model.TokenResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthService {
    @GET("api/v1/google/code")
    suspend fun getCode(@Query("code") authCode: String): TokenResponse

    @GET("api/v1/google/login")
    suspend fun getToken(@Query("token") idToken: String): LoginTokenResponse

    @GET("api/v1/google/login/reissue")
    suspend fun reissueToken(@Query("RTK") refreshToken: String): LoginTokenResponse
}