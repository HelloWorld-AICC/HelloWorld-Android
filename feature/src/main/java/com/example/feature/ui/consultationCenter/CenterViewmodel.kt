package com.example.feature.ui.consultationCenter

import androidx.lifecycle.ViewModel
import com.example.core.data.centerInfo.CenterInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CenterViewModel : ViewModel() {

    // 선택된 상담센터 상태
    private val _selectedCenter = MutableStateFlow<CenterInfo?>(null)
    val selectedCenter: StateFlow<CenterInfo?> = _selectedCenter.asStateFlow()

    fun selectCenter(center: CenterInfo?) {
        _selectedCenter.value = center
    }
}
