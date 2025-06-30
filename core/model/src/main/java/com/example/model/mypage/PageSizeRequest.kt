package com.example.model.mypage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageSizeRequest(
    @SerialName("page")
    val page: Int,
    @SerialName("size")
    val size: Int,
)
