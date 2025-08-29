package com.example.model.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailResponse(
    @SerialName("title")
    val title: String = "",
    @SerialName("content")
    val content: String = "",
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("fileList")
    val fileList: List<CommunityDetailFile> = emptyList(),
    @SerialName("commentDTOList")
    val commentList: List<DetailComment> = emptyList(),
    @SerialName("isOwner")
    val isOwner: Boolean = false,
)

@Serializable
data class DetailComment(
    @SerialName("commentId")
    val commentId: Long,
    @SerialName("anonymousName")
    val anonymousName: Long,        // 익명 + 반환값 표시
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("content")
    val content: String,
    @SerialName("isOwner")
    val isOwner: Boolean = false,
)

@Serializable
data class CommunityDetailFile(
    @SerialName("fileUrl")
    val fileUrl: String,
    @SerialName("fileType")
    val fileType: String = "jpg"
)