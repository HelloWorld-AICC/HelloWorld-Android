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
import kotlin.math.*

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
    private var radiusMeters: Double = 100_000.0  // 🔵 반경 100km (기본)

    fun selectCenter(center: Center?) {
        _selectedCenter.value = center
    }

    /** 중심점/반경 변경 시 초기화 + 첫 페이지 로드 */
    fun resetAndLoad(latitude: Double, longitude: Double, radiusMeters: Float = 100_000f) {
        lastLat = latitude
        lastLng = longitude
        this.radiusMeters = radiusMeters.toDouble()
        page = 0
        endReached = false
        _centerList.value = emptyList()
        loadNextPage()
    }

    fun loadNextPage() {
        val lat = lastLat ?: return
        val lng = lastLng ?: return
        if (_isLoading.value || endReached) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("CenterViewModel", "요청: page=$page, size=$pageSize, lat=$lat, lng=$lng, r=${radiusMeters}m")

                // ✅ 서버가 반경 파라미터를 지원한다면 주석 해제해서 서버 필터 사용:
                // val response = RetrofitInstance.centerService.getCenterInfo(
                //     page = page, size = pageSize, latitude = lat, longitude = lng, radiusKm = (radiusMeters / 1000.0).roundToInt()
                // )
                val response = RetrofitInstance.centerService.getCenterInfo(
                    page = page, size = pageSize, latitude = lat, longitude = lng
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.isSuccess == true) {
                        val raw = body.result?.centerMapList.orEmpty()

                        // 🔵 클라이언트 반경 필터(100km)
                        val filtered = raw.filter { c ->
                            distanceMeters(lat, lng, c.latitude, c.longitude) <= radiusMeters
                        }

                        // 누적
                        _centerList.value = _centerList.value + filtered

                        // 다음 요청 여부(서버 페이지 기준으로 판단)
                        if (raw.size < pageSize) {
                            endReached = true
                        } else {
                            page += 1
                        }

                        Log.d(
                            "CenterViewModel",
                            "로드 완료: nextPage=$page, 누적=${_centerList.value.size}, 추가(raw=${raw.size}, inRadius=${filtered.size}), end=$endReached"
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

    /** Haversine: 두 좌표 사이 거리(m) */
    private fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371_000.0 // m
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}
