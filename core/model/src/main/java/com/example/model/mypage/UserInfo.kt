package com.example.model.mypage

import com.example.model.common.Language
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    @SerialName("name")
    val name: String,
    @SerialName("userImg")
    val userImg: String?,
    @SerialName("language")
    val language: Language? = null
)
