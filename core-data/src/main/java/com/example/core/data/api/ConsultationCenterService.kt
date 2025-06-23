package com.example.core.data.api

import com.example.core.data.model.ConsultationCenterResponse
import retrofit2.http.GET

interface ConsultationCenterService {
    @GET("center/")
    suspend fun getCenterInfo(): ConsultationCenterResponse
}