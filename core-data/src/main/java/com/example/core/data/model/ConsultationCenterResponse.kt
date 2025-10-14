package com.example.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsultationCenterResponse(
    @SerialName("centerMapList") val centerMapList: List<Center>,
    @SerialName("userId")        val userId: String
)

@Serializable
data class Center(
    @SerialName("centerId")  val centerId: Int,
    @SerialName("name")      val name: String,
    @SerialName("status")    val status: String,
    @SerialName("closed")    val closed: String,
    @SerialName("address")   val address: String,
    @SerialName("image")     val image: String? = null,   // ← 여기!
    @SerialName("latitude")  val latitude: Double,
    @SerialName("longitude") val longitude: Double
)