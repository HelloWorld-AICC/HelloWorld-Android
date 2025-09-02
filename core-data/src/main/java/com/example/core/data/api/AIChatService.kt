package com.example.core.data.api

import com.example.core.data.model.aichat.AIChatLogResponse
import com.example.core.data.model.aichat.ChattingRoom
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming


interface AIChatService {

    @POST("summary")
    suspend fun summaryAIChat(
        @Query("roomId") roomId : String
    ) : ResponseBody

    @POST("chat/ask")
    @Streaming
    suspend fun askToAI(
        @Query("roomId") roomId : String,
        @Body request: RequestBody
    ) : Response<ResponseBody>

    @GET("user/room-list")
    suspend fun getAIChattingRooms() : List<ChattingRoom>

    @GET("chat/room-log")
    suspend fun getAIChatLog(
        @Query("roomId") roomId : String
    ) : AIChatLogResponse
}