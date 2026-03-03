package com.example.core.data.api

import com.example.core.data.model.aichat.AIChatLogResponse
import com.example.core.data.model.aichat.AskToAIRequest
import com.example.core.data.model.aichat.ChattingRoomsResponse
import com.example.core.data.model.aichat.CreateChatRoomResponse
import com.example.core.data.model.aichat.SummaryAIChatResponse
import com.example.core.data.model.aichat.SummaryChattingRoomsResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming

interface AIChatService {

    @POST("api/chat/create-room")
    suspend fun createChatRoom(): Response<CreateChatRoomResponse>

    @POST("/api/summary")
    suspend fun summaryAIChat(
        @Query("roomId") roomId: String
    ): Response<SummaryAIChatResponse>

    @Streaming
    @Headers(
        "Content-Type: application/json",
        "Cache-Control: no-cache",
        "Connection: keep-alive",
        "Accept-Encoding: identity"
    )
    @POST("/api/chat/ask")
    suspend fun askToAI(
        @Query("roomId") roomId: String,
        @Body request: AskToAIRequest
    ): Response<ResponseBody>

    @GET("/api/chat/user-rooms")
    suspend fun getAIChattingRooms(): Response<ChattingRoomsResponse>

    @GET("/api/chat/room-log")
    suspend fun getAIChatLog(
        @Query("roomId") roomId: String
    ): Response<AIChatLogResponse>

    @GET("/api/chat/user-summaries")
    suspend fun getSummaryList(): Response<SummaryChattingRoomsResponse>
}
