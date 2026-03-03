package com.example.core.data.model.aichat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SummaryAIChatResponse(
    @SerialName("timestamp") val timestamp: String,
    @SerialName("path") val path: String,
    @SerialName("status") val status: Int,
    @SerialName("error") val error: String,
    @SerialName("requestId") val requestId: String,
    @SerialName("data") val data: SummaryAIChatData
)

@Serializable
data class SummaryAIChatData(
    @SerialName("summary") val summary: String
)
