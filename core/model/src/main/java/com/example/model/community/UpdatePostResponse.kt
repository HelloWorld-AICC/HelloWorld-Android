package com.example.model.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePostResponse(
//    @SerialName("post_id")
//    val postId: Long,
//    @SerialName("categoryName")
//    val categoryName: String,
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String,
    @SerialName("communityWriterEmail")
    val communityWriterEmail: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("isOwner")
    val isOwner: Boolean,
)
