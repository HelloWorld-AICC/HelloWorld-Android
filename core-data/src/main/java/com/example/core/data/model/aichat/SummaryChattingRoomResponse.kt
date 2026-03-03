package com.example.core.data.model.aichat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SummaryChattingRoom(
    @SerialName("roomId") val roomId : String,
    @SerialName("roomTitle") val title : String? = null,
    @SerialName("createdAt") val createdAt : String? = null,
    @SerialName("roomSummary") val roomSummary : String? = null,
    @SerialName("updatedAt") val updatedAt : String? = null
)

@Serializable
data class SummaryChattingRoomsResponse(
    @SerialName("timestamp") val timestamp: String,
    @SerialName("path") val path: String,
    @SerialName("status") val status: Int,
    @SerialName("error") val error: String,
    @SerialName("requestId") val requestId: String,
    @SerialName("data") val data: SummaryChattingRoomsData
)

@Serializable
data class SummaryChattingRoomsData(
    @SerialName("summaries") val rooms: List<SummaryChattingRoom> = emptyList()
)
