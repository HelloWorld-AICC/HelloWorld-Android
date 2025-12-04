package com.example.model.mypage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailSummaryResponse(
    @SerialName("summaryId")
    val summaryId: Long,
    @SerialName("identificationNum")
    val identificationNum: String,
    @SerialName("uploadedAt")
    val uploadedAt: String,
    @SerialName("title")
    val title: String,
    @SerialName("name")
    val name: String,
    @SerialName("userImg")
    val userImg: String?,
    @SerialName("chatSummary")
    val chatSummary: String,
    @SerialName("mainPoint")
    val mainPoint: String?,
    @SerialName("roomId")
    val roomId: String,
)
