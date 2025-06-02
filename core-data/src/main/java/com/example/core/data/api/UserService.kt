package com.example.core.data.api

import com.example.core.data.model.MyPageResponse
import retrofit2.http.GET

interface UserService {
    @GET("myPage/")
    suspend fun getMyPage(): MyPageResponse
}