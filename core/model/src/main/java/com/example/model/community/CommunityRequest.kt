package com.example.model.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommunityRequest(
    @SerialName("page")
    val page: Int = 0,
    @SerialName("size")
    val size: Int = 10,
    @SerialName("category_id")
    val categoryId: Long = 0,       // 0: 직장 내 고충, 1: 산재 및 의료, 2: 체류 및 근로 자격, 3: 기타
)
