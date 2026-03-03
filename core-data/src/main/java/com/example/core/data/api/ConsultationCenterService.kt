package com.example.core.data.api

import com.example.core.data.model.ConsultationCenterResponse
import com.example.model.community.CommunityResponse
import com.example.network.response.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ConsultationCenterService {
    @GET("center/")
    suspend fun getCenterInfo(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<ApiResponse<ConsultationCenterResponse>>
}
