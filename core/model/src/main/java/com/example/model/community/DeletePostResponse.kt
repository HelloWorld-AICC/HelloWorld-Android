package com.example.model.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeletePostResponse(
    @SerialName("post_id")
    val postId: Long,
    @SerialName("categoryName")
    val categoryName: String,
)
