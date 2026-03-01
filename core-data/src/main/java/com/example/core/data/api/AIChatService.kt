package com.example.core.data.api

import com.example.core.data.model.aichat.AIChatLogResponse
import com.example.core.data.model.aichat.AskToAIRequest
import com.example.core.data.model.aichat.ChattingRoom
import com.example.core.data.model.aichat.CreateChatRoomResponse
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

    @Headers("Accept: text/plain") // 문자열 응답 힌트 (선택)
    @POST("summary")
    suspend fun summaryAIChat(
        @Query("roomId") roomId: String
    ): Response<ResponseBody>

    @Streaming
    @Headers(
        "Content-Type: application/json",
        "Cache-Control: no-cache",
        "Connection: keep-alive",
        "Accept-Encoding: identity"
    )
    @POST("/api/chat/ask")
    suspend fun askToAI(
        @Query("roomId") roomId : String,
        @Body request: AskToAIRequest
    ) : Response<ResponseBody>

    @GET("user/room-list")
    suspend fun getAIChattingRooms() : Response<List<ChattingRoom>>

    @GET("/api/chat/room-log")
    suspend fun getAIChatLog(
        @Query("roomId") roomId : String
    ) :  Response<AIChatLogResponse>
}
