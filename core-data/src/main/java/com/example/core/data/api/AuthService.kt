// Retrofit 인터페이스 정의

package com.example.core.data.api

import com.example.core.data.model.LoginTokenResponse
import com.example.core.data.model.TokenResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthService {
    // 구글 로그인 인증코드 발급
    @GET("api/v1/google/code")
    suspend fun getCode(@Query("code") authCode: String): TokenResponse

    // 구글 로그인
    @GET("api/v1/google/login")
    suspend fun getToken(@Query("token") idToken: String): LoginTokenResponse

    // 구글 로그인 토큰 재발급 api
    @GET("api/v1/google/login/reissue")
    suspend fun reissueToken(@Query("RTK") refreshToken: String): LoginTokenResponse
}