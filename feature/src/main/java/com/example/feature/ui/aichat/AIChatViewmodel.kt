package com.example.feature.ui.aichat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.model.aichat.ChattingRoom
import com.example.core.data.network.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

@HiltViewModel
class AIChatViewModel @Inject constructor() : ViewModel() {

    private val _chattingRooms = MutableStateFlow<List<ChattingRoom>>(emptyList())
    val chattingRooms: StateFlow<List<ChattingRoom>> = _chattingRooms

    private val _createdChatRoomId = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val createdChatRoomId: SharedFlow<String> = _createdChatRoomId.asSharedFlow()

    fun createChatRoom() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.aiChatService.createChatRoom()

                if (response.isSuccessful) {
                    val roomId = response.body()?.data?.roomId
                    if (!roomId.isNullOrBlank()) {
                        _createdChatRoomId.tryEmit(roomId)
                        Log.d(TAG, "새 채팅방 생성 성공: $roomId")
                    } else {
                        Log.e(TAG, "새 채팅방 생성 응답에 roomId가 없음")
                    }
                } else {
                    val code = response.code()
                    val msg = runCatching { response.errorBody()?.string() }.getOrNull()
                    Log.e(TAG, "새 채팅방 생성 실패: HTTP $code ${msg.orEmpty()}")
                }
            } catch (e: IOException) {
                Log.e(TAG, "네트워크 오류로 새 채팅방 생성 실패: ${e.message}", e)
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP 예외로 새 채팅방 생성 실패: ${e.message}", e)
            } catch (e: Exception) {
                Log.e(TAG, "알 수 없는 오류로 새 채팅방 생성 실패: ${e.message}", e)
            }
        }
    }

    fun getAIChattingRooms() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.aiChatService.getAIChattingRooms()

                if (response.isSuccessful) {
                    val rooms = response.body()
                    if (rooms != null) {
                        _chattingRooms.value = rooms
                        Log.d(TAG, "채팅방 목록 로드 성공: ${rooms.size}개")
                    }
                } else {
                    val code = response.code()
                    val msg = runCatching { response.errorBody()?.string() }.getOrNull()
                    Log.e(TAG, "채팅방 목록 로드 실패: HTTP $code ${msg.orEmpty()}")
                    _chattingRooms.value = emptyList()
                }
            } catch (e: IOException) {
                Log.e(TAG, "네트워크 오류로 채팅방 목록 로드 실패: ${e.message}", e)
                _chattingRooms.value = emptyList()
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP 예외로 채팅방 목록 로드 실패: ${e.message}", e)
                _chattingRooms.value = emptyList()
            } catch (e: Exception) {
                Log.e(TAG, "알 수 없는 오류로 채팅방 목록 로드 실패: ${e.message}", e)
                _chattingRooms.value = emptyList()
            }
        }
    }

    companion object {
        private const val TAG = "AIChatViewModel"
    }
}
