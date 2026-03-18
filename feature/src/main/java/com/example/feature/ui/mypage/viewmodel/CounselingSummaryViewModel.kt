package com.example.feature.ui.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.model.aichat.SummaryChattingRoom
import com.example.core.data.network.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CounselingSummaryViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<CounselingSummaryUiState>(CounselingSummaryUiState.Loading)
    val uiState: StateFlow<CounselingSummaryUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData = _hasMoreData.asStateFlow()

    private var currentPage = 0
    private val currentSummaryList = mutableListOf<SummaryChattingRoom>()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        currentPage = 0
        currentSummaryList.clear()
        loadSummaryPage()
    }

    fun loadNextPage() {
        if (_isLoading.value || !_hasMoreData.value) return
        loadSummaryPage()
    }

    private fun loadSummaryPage() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.aiChatService.getSummaryList()
                if (!response.isSuccessful) {
                    _uiState.value = CounselingSummaryUiState.Error("HTTP ${response.code()}")
                    return@launch
                }

                val newItems = response.body()
                    ?.data
                    ?.rooms
                    .orEmpty()
                    .filter { it.roomSummary != null }

                if (currentPage == 0) {
                    currentSummaryList.clear()
                }
                currentSummaryList.addAll(newItems)

                _hasMoreData.value = false
                currentPage++
                _uiState.value = CounselingSummaryUiState.Success(currentSummaryList.toList())
            } catch (e: Exception) {
                _uiState.value = CounselingSummaryUiState.Error(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }

}

sealed interface CounselingSummaryUiState {
    data object Loading : CounselingSummaryUiState
    data class Success(val result: List<SummaryChattingRoom>) : CounselingSummaryUiState
    data class Error(val msg: String) : CounselingSummaryUiState
}
