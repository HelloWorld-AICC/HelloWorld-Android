package com.example.model.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    @SerialName("post_id")
    val postId: Int,
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("commentNum")
    val commentNum: Int,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("category_id")
    val categoryId: Long,
)

@Serializable
data class CommunityResponse(
    @SerialName("postDTOList")
    val postList: List<Post> = emptyList()
)
