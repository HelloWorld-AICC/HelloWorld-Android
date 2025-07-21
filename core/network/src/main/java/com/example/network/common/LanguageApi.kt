package com.example.network.common

import com.example.model.common.LanguageRequest
import com.example.network.response.ApiResponse
import com.example.network.retrofit.ApiConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface LanguageApi {
    @POST(ApiConstants.SET_LANGUAGE)
    suspend fun setLanguage(
        @Path("language_id") id: Long,
        @Body request: LanguageRequest
    ): Response<ApiResponse<String>>
}