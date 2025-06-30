package com.example.model.mypage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailSummaryRequest(
    @SerialName("summary-id")
    val summaryId: Long,
)
