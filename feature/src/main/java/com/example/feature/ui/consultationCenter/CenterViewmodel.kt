// com/example/feature/ui/consultationCenter/CenterViewModel.kt
package com.example.feature.ui.consultationCenter

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.model.Center
import com.example.core.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CenterViewModel : ViewModel() {

    // 선택된 센터 상태
    private val _selectedCenter = MutableStateFlow<Center?>(null)
    val selectedCenter: StateFlow<Center?> = _selectedCenter

    // 전체 센터 목록 상태
    private val _centerList = MutableStateFlow<List<Center>>(emptyList())
    val centerList: StateFlow<List<Center>> = _centerList

    fun selectCenter(center: Center?) {
        _selectedCenter.value = center
    }

    fun fetchCenterList(
        page: Int = 0,
        size: Int = 20,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.centerService.getCenterInfo(
                    page = page,
                    size = size,
                    latitude = latitude,
                    longitude = longitude
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.isSuccess == true) {
                        val centers = body.result?.centerMapList
                        if (centers != null) {
                            _centerList.value = centers
                        }
                        Log.d("CenterViewModel", "센터 정보 로드: ${centers?.size}개")
                        Log.d("CenterViewModel", "센터 목록: $centers")
                    } else {
                        Log.w(
                            "CenterViewModel",
                            "API isSuccess=false: code=${body?.code}, msg=${body?.message}"
                        )
                    }
                } else {
                    Log.w(
                        "CenterViewModel",
                        "HTTP 실패: ${response.code()} ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("CenterViewModel", "센터 정보 API 실패: ${e.message}", e)
            }
        }
    }
}
