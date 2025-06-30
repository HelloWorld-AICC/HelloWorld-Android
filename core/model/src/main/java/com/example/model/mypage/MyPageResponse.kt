package com.example.model.mypage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyPageResponse(
    @SerialName("name")
    val name: String,
    @SerialName("userImg")
    val userImg: String?
)
