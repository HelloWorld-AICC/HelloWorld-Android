package com.example.model.mypage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Summary(
    @SerialName("summaryId")
    val summaryId: Long,
    @SerialName("identificationNum")
    val identificationNum: String,
    @SerialName("uploadedAt")
    val uploadedAt: String,
    @SerialName("name")
    val name: String?,
    @SerialName("userImg")
    val userImg: String?,
    @SerialName("title")
    val title: String,
)

@Serializable
data class AllSummaryResponse(
    @SerialName("userId")
    val userId: String,
    @SerialName("allsummaryList")
    val allSummaryList: List<Summary>
)