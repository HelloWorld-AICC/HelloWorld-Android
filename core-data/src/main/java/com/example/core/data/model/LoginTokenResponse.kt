// 구글 로그인 응답 모델 정의 (/api/v1/google/login)

package com.example.core.data.model

data class LoginTokenItem (
    val types: String,
    val token: String,
    val tokenExpriresTime: String,
)

data class LoginTokenResult(
    val tokenList: List<LoginTokenItem>
)

data class LoginTokenResponse (
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: LoginTokenResult,
)

