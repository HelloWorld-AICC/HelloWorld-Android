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
    @SerialName("korName")      val korName: String,
    @SerialName("korAddress")   val korAddress: String,
    @SerialName("usaName")      val usaName: String,
    @SerialName("usaAddress")   val usaAddress: String,
    @SerialName("jpnName")      val jpnName: String,
    @SerialName("jpnAddress")   val jpnAddress: String,
    @SerialName("chnName")      val chnName: String,
    @SerialName("chnAddress")   val chnAddress: String,
    @SerialName("vnmName")      val vnmName: String,
    @SerialName("vnmAddress")   val vnmAddress: String,
    @SerialName("status")    val status: String,
    @SerialName("closed")    val closed: String,
    @SerialName("image")     val image: String? = null,
    @SerialName("latitude")  val latitude: Double,
    @SerialName("longitude") val longitude: Double
)