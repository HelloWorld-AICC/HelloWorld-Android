package com.example.model.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WriteResponse(
    @SerialName("community_id")
    val communityId: Long,
)
