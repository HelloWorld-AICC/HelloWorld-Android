package com.example.feature.ui.consultationCenter

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.model.Center
import com.example.core.data.model.ConsultationCenterResponse
import com.example.core.data.network.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CenterViewModel @Inject constructor() : ViewModel() {

    // 선택된 센터 상태
    private val _selectedCenter = MutableStateFlow<Center?>(null)
    val selectedCenter: StateFlow<Center?> = _selectedCenter

    // 전체 센터 목록 상태
    private val _centerList = MutableStateFlow<List<Center>>(emptyList())
    val centerList: StateFlow<List<Center>> = _centerList

    // 유저 ID (필요한 경우)
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    fun selectCenter(center: Center?) {
        _selectedCenter.value = center
    }

    fun fetchCenterListIfTokenExists() {
        val token = RetrofitInstance.getAccessToken()
        if (token.isNotBlank()) {
            fetchCenterList()
        } else {
            Log.w("CenterViewModel", "토큰 없음 - 센터 정보 요청 보류")
        }
    }

    fun fetchCenterList() {
        viewModelScope.launch {
            try {
                val response: ConsultationCenterResponse =
                    RetrofitInstance.centerService.getCenterInfo()

                if (response.isSuccess) {
                    _centerList.value = response.result.centerMapList
                    _userId.value = response.result.userId
                    Log.d("CenterViewModel", "센터 정보 성공적으로 로드됨: ${response.result.centerMapList.size}개")
                } else {
                    Log.w("CenterViewModel", "센터 정보 응답 실패: ${response.message}")
                }

            } catch (e: Exception) {
                Log.e("CenterViewModel", "센터 정보 API 실패: ${e.message}")
            }
        }
    }
}
