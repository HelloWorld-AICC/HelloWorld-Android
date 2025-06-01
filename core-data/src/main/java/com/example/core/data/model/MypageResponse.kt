// 마이페이지 응답 모델 정의 (/myPage)

package com.example.core.data.model

data class MyPageResult(
    val userId: Int,
    val name: String,
    val userImg: String
)

data class MyPageResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: MyPageResult
)