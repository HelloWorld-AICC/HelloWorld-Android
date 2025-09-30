package com.example.core.data.model.aichat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChattingRoom(
    @SerialName("roomId") val roomId : String,
    @SerialName("title") val title : String,
    @SerialName("updatedAt") val updatedAt : String
)