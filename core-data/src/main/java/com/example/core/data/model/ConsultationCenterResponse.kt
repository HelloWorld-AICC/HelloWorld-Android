package com.example.core.data.model

data class ConsultationCenterResponse (
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ConsultationCenterResult
)

data class ConsultationCenterResult(
    val centerMapList: List<Center>,
    val userId : String
)

data class Center (
    val centerId : Int,
    val name : String,
    val status : String,
    val closed : String,
    val address : String,
    val image : String,
    val latitude : Double,
    val longitude : Double
)
