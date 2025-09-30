package com.example.feature.ui.aichat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.model.aichat.ChattingRoom
import com.example.core.data.network.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject // ⬅️ jakarta 아니라 javax 사용 권장 (Hilt 표준)
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@HiltViewModel
class AIChatViewModel @Inject constructor() : ViewModel() {

    private val _chattingRooms = MutableStateFlow<List<ChattingRoom>>(emptyList())
    val chattingRooms: StateFlow<List<ChattingRoom>> = _chattingRooms

    fun getAIChattingRooms() {
        viewModelScope.launch {
            try {
                // 인터셉터로 Authorization 추가 중이면 아래 파라미터는 제거하세요.
                val response = RetrofitInstance.aiChatService.getAIChattingRooms()

                if (response.isSuccessful) {
                    val rooms = response.body()?.result
                    if (rooms != null) {
                        _chattingRooms.value = rooms
                        Log.d(TAG, "채팅방 목록 로드 성공: ${rooms.size}개")
                    }
                } else {
                    val code = response.code()
                    val msg = runCatching { response.errorBody()?.string() }.getOrNull()
                    Log.e(TAG, "채팅방 목록 로드 실패: HTTP $code ${msg ?: ""}")
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
