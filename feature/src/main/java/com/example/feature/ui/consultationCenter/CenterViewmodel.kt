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

    private val _selectedCenter = MutableStateFlow<Center?>(null)
    val selectedCenter: StateFlow<Center?> = _selectedCenter

    private val _centerList = MutableStateFlow<List<Center>>(emptyList())
    val centerList: StateFlow<List<Center>> = _centerList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 페이지네이션/쿼리 상태
    private var page = 0
    private val pageSize = 20
    private var endReached = false
    private var lastLat: Double? = null
    private var lastLng: Double? = null

    fun selectCenter(center: Center?) {
        _selectedCenter.value = center
    }

    /** 중심점 변경 시 초기화 + 첫 페이지 로드 (반경 제한 없음) */
    fun resetAndLoad(latitude: Double, longitude: Double) {
        lastLat = latitude
        lastLng = longitude
        page = 0
        endReached = false
        _centerList.value = emptyList()
        loadNextPage()
    }

    /** 바닥 스크롤 시 다음 페이지 로드 */
    fun loadNextPage() {
        val lat = lastLat ?: return
        val lng = lastLng ?: return
        if (_isLoading.value || endReached) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("CenterViewModel", "요청: page=$page, size=$pageSize, lat=$lat, lng=$lng")

                val response = RetrofitInstance.centerService.getCenterInfo(
                    page = page, size = pageSize, latitude = lat, longitude = lng
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.isSuccess == true) {
                        val newList = body.result?.centerMapList.orEmpty()

                        // 누적
                        _centerList.value = _centerList.value + newList

                        // 다음 요청 여부(서버 페이지 크기 기준)
                        if (newList.size < pageSize) {
                            endReached = true
                        } else {
                            page += 1
                        }

                        Log.d(
                            "CenterViewModel",
                            "로드 완료: nextPage=$page, 누적=${_centerList.value.size}, 추가=${newList.size}, end=$endReached"
                        )
                    } else {
                        Log.w("CenterViewModel", "API isSuccess=false: code=${body?.code}, msg=${body?.message}")
                    }
                } else {
                    Log.w("CenterViewModel", "HTTP 실패: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CenterViewModel", "센터 정보 API 실패: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
