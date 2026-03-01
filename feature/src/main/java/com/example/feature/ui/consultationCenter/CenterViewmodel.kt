package com.example.feature.ui.consultationCenter

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.common.LanguageRepository
import com.example.core.data.model.Center
import com.example.core.data.network.RetrofitInstance
import com.example.model.common.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class CenterViewModel @Inject constructor(
    private val languageRepository: LanguageRepository,
) : ViewModel() {

    // ---------------------------
    // 상태(원본)
    // ---------------------------

    private val _centerList = MutableStateFlow<List<Center>>(emptyList())
    val centerList: StateFlow<List<Center>> = _centerList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _language = MutableStateFlow(Language.KOREAN)
    val language: StateFlow<Language> = _language

    // ✅ 선택은 id만 저장(정형화)
    private val _selectedCenterId = MutableStateFlow<Int?>(null)
    val selectedCenterId: StateFlow<Int?> = _selectedCenterId

    // ---------------------------
    // UI 표시용 모델
    // ---------------------------

    data class CenterDisplay(
        val centerId: Int,
        val name: String,
        val address: String,
        val latitude: Double,
        val longitude: Double,
        val image: String?,
        val isOpenNow: Boolean,   // ✅ "09:00 ~ closed" 로 계산된 영업 여부
        val closed: String
    )

    // ✅ UI 리스트(언어 반영 + 영업여부 계산)
    val displayCenterList: StateFlow<List<CenterDisplay>> =
        combine(centerList, language) { centers, lang ->
            centers.map { it.toDisplay(lang) }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    // ✅ 선택된 센터(원본)
    val selectedCenter: StateFlow<Center?> =
        combine(centerList, selectedCenterId) { centers, id ->
            id?.let { centerId -> centers.firstOrNull { it.centerId == centerId } }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // ✅ 선택된 센터(UI용, 언어 반영 + 영업여부 계산)
    val selectedDisplayCenter: StateFlow<CenterDisplay?> =
        combine(selectedCenter, language) { center, lang ->
            center?.toDisplay(lang)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // ---------------------------
    // 페이지네이션/쿼리
    // ---------------------------

    private var page = 0
    private val pageSize = 20
    private var endReached = false
    private var lastLat: Double? = null
    private var lastLng: Double? = null

    init {
        loadLanguage()
    }

    fun selectCenter(centerId: Int?) {
        _selectedCenterId.value = centerId
    }

    /** 중심점 변경 시 초기화 + 첫 페이지 로드 */
    fun resetAndLoad(latitude: Double, longitude: Double) {
        lastLat = latitude
        lastLng = longitude

        page = 0
        endReached = false
        _selectedCenterId.value = null
        _centerList.value = emptyList()

        loadNextPage()
    }

    private fun loadLanguage() {
        viewModelScope.launch {
            runCatching { languageRepository.getLanguage() }
                .onSuccess { _language.value = it }
                .onFailure {
                    _language.value = Language.KOREAN
                    Log.e("CenterViewModel", "getLanguageFail", it)
                }
        }
    }

    /** 다음 페이지 로드 */
    fun loadNextPage() {
        val lat = lastLat ?: return
        val lng = lastLng ?: return
        if (_isLoading.value || endReached) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("CenterViewModel", "요청: page=$page, size=$pageSize, lat=$lat, lng=$lng")

                val response = RetrofitInstance.centerService.getCenterInfo(
                    page = page,
                    size = pageSize,
                    latitude = lat,
                    longitude = lng
                )

                if (!response.isSuccessful) {
                    Log.w("CenterViewModel", "HTTP 실패: ${response.code()} ${response.message()}")
                    return@launch
                }

                val body = response.body()
                if (body?.isSuccess != true) {
                    Log.w("CenterViewModel", "API isSuccess=false: code=${body?.code}, msg=${body?.message}")
                    return@launch
                }

                val newList = body.result?.centerMapList.orEmpty()
                _centerList.update { it + newList }

                if (newList.size < pageSize) endReached = true else page += 1

                Log.d(
                    "CenterViewModel",
                    "로드 완료: nextPage=$page, 누적=${_centerList.value.size}, 추가=${newList.size}, end=$endReached"
                )
            } catch (e: Exception) {
                Log.e("CenterViewModel", "센터 정보 API 실패: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ---------------------------
    // 영업 여부 계산
    // ---------------------------

    private val seoulZone: ZoneId = ZoneId.of("Asia/Seoul")
    private val openStart: LocalTime = LocalTime.of(9, 0)

    private fun isOpenNow(closed: String): Boolean {
        val now = LocalTime.now(seoulZone)
        val closeTime = parseCloseTime(closed) ?: run {
            // closed 파싱이 안 되면, 안전하게 "영업 종료"로 처리(원하면 true로 바꿔도 됨)
            return false
        }

        // ✅ 09:00 이상 AND closeTime 이전이면 영업중
        // - 09:00은 포함 (>=)
        // - closeTime은 미포함 (<) : "18:00"이면 18:00 딱 되면 종료
        return !now.isBefore(openStart) && now.isBefore(closeTime)
    }

    /**
     * closed 문자열 파싱
     * 지원 예:
     * - "18:00", "9:30"
     * - "1800", "0930"
     * - "18", "9"
     * - "24:00", "2400" -> 23:59:59 로 처리(자정 직전까지)
     */
    private fun parseCloseTime(raw: String?): LocalTime? {
        val s = raw?.trim().orEmpty()
        if (s.isBlank()) return null

        // 24:00 / 2400 처리 (LocalTime은 24:00 불가)
        if (s == "24:00" || s == "2400") return LocalTime.of(23, 59, 59)

        // HH:mm
        if (s.contains(":")) {
            val parts = s.split(":")
            if (parts.size >= 2) {
                val h = parts[0].toIntOrNull() ?: return null
                val m = parts[1].toIntOrNull() ?: return null
                if (h !in 0..23 || m !in 0..59) return null
                return LocalTime.of(h, m)
            }
        }

        // HHmm (4자리)
        if (s.length == 4 && s.all { it.isDigit() }) {
            val h = s.substring(0, 2).toIntOrNull() ?: return null
            val m = s.substring(2, 4).toIntOrNull() ?: return null
            if (h !in 0..23 || m !in 0..59) return null
            return LocalTime.of(h, m)
        }

        // H or HH
        if (s.length in 1..2 && s.all { it.isDigit() }) {
            val h = s.toIntOrNull() ?: return null
            if (h !in 0..23) return null
            return LocalTime.of(h, 0)
        }

        return null
    }

    // ---------------------------
    // 변환(정형화)
    // ---------------------------

    private fun Center.toDisplay(language: Language): CenterDisplay {
        val name = when (language) {
            Language.KOREAN -> korName
            Language.ENGLISH -> usaName
            Language.JAPANESE -> jpnName
            Language.CHINESE -> chnName
            Language.VIETNAMESE -> vnmName
        }

        val address = when (language) {
            Language.KOREAN -> korAddress
            Language.ENGLISH -> usaAddress
            Language.JAPANESE -> jpnAddress
            Language.CHINESE -> chnAddress
            Language.VIETNAMESE -> vnmAddress
        }

        return CenterDisplay(
            centerId = centerId,
            name = name,
            address = address,
            latitude = latitude,
            longitude = longitude,
            image = image,
            isOpenNow = isOpenNow(closed),
            closed = closed
        )
    }
}
