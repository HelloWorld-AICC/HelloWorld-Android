// 응답 모델 정의

package com.example.core.data.model

data class TokenItem(
    val types: String,
    val token: String,
    val tokenExpriresTime: String
)

data class TokenResult(
    val tokenList: List<TokenItem>
)

data class TokenResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: TokenResult
)