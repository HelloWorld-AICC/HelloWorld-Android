package com.example.core.data.model

data class ChattingRoomsResponse(
    val rooms : List<ChattingRoom>
)

data class ChattingRoom(
    val roomId : String,
    val title : String
)