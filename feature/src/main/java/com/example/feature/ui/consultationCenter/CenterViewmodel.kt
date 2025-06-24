package com.example.feature.ui.consultationCenter

import androidx.lifecycle.ViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class CenterInfo (
    val name : String,
    val status : String,
    val address : String,
    val latitude : Double,
    val longitude : Double
)

class CenterViewModel : ViewModel() {

    // 선택된 상담센터 상태
    private val _selectedCenter = MutableStateFlow<CenterInfo?>(null)
    val selectedCenter: StateFlow<CenterInfo?> = _selectedCenter.asStateFlow()

    fun selectCenter(center: CenterInfo?) {
        _selectedCenter.value = center
    }
}
