package com.example.model.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailRequest(
    @SerialName("page")
    val page: Int = 0,
    @SerialName("size")
    val size: Int = 10,
    @SerialName("community_id")
    val communityId: Long,
)
