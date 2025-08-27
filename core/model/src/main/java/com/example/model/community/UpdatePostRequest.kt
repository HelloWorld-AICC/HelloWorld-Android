package com.example.model.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePostRequest(
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String,
)
