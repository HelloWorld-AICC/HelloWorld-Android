package com.example.model.mypage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Community(
    @SerialName("communityId")
    val communityId: Long,
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String,
    @SerialName("uploadedAt")
    val uploadedAt: String,
    @SerialName("category")
    val category: Long,
    @SerialName("commentCnt")
    val commentCnt: Int,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
)

@Serializable
data class AllCommunityResponse(
    @SerialName("userId")
    val userId: String,
    @SerialName("allMyCommunityList")
    val allMyCommunityList: List<Community>
)