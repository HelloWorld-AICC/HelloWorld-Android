package com.example.model.mypage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    @SerialName("categoryId")
    val categoryId: Long,
    @SerialName("communityId")
    val communityId: Long,
    @SerialName("commentId")
    val commentId: Long,
    @SerialName("commentContent")
    val commentContent: String,
    @SerialName("uploadedAt")
    val uploadedAt: String,
    @SerialName("communityTitle")
    val communityTitle: String,
)

@Serializable
data class AllCommentResponse(
    @SerialName("userId")
    val userId: String,
    @SerialName("allMyCommentList")
    val allMyCommentList: List<Comment>
)