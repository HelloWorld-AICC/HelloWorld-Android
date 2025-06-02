// 구글 로그인 인증코드 응답 모델 정의 (/api/v1/google/code)

package com.example.core.data.model

data class TokenItem(
    val types: String,
    val token: String,
    val tokenExpriresTime: String
)

data class TokenResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: String
)