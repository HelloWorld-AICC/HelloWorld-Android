package com.example.model.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteCommentResponse(
    @SerialName("commentId")
    val commentId: Long,
    @SerialName("communityId")
    val communityId: Long,
)
