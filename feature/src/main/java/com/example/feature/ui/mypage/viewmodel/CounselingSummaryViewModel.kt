package com.example.feature.ui.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.mypage.MyPageRepository
import com.example.model.mypage.PageSizeRequest
import com.example.model.mypage.Summary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CounselingSummaryViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CounselingSummaryUiState>(CounselingSummaryUiState.Loading)
    val uiState: StateFlow<CounselingSummaryUiState> = _uiState.asStateFlow()

    init {
        getAllSummary()
    }

    fun getAllSummary(page: Int = 1, size: Int = 10) {
        viewModelScope.launch {
            _uiState.value = CounselingSummaryUiState.Loading
            myPageRepository.getAllSummary(
                PageSizeRequest(
                    page = page,
                    size = size,
                )
            ).fold(
                onSuccess = {
                    _uiState.value = CounselingSummaryUiState.Success(it.allSummaryList)
                },
                onFailure = {
                    _uiState.value = CounselingSummaryUiState.Error("${it.message}")

                }
            )
        }
    }
}

sealed interface CounselingSummaryUiState {
    data object Loading : CounselingSummaryUiState
    data class Success(val result: List<Summary>) : CounselingSummaryUiState
    data class Error(val msg: String) : CounselingSummaryUiState
}