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

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData = _hasMoreData.asStateFlow()

    private var currentPage = 0
    private val currentSummaryList = mutableListOf<Summary>()

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

            myPageRepository.getAllSummary(
                PageSizeRequest(
                    page = currentPage,
                    size = PAGE_SIZE,
                )
            ).fold(
                onSuccess = { response ->
                    val newItems = response.allSummaryList
                    currentSummaryList.addAll(newItems)

                    _hasMoreData.value = newItems.size == PAGE_SIZE

                    if (_hasMoreData.value) {
                        currentPage++
                    }

                    _uiState.value = CounselingSummaryUiState.Success(currentSummaryList.toList())
                },
                onFailure = {
                    _uiState.value = CounselingSummaryUiState.Error("${it.message}")
                }
            )

            _isLoading.value = false
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}

sealed interface CounselingSummaryUiState {
    data object Loading : CounselingSummaryUiState
    data class Success(val result: List<Summary>) : CounselingSummaryUiState
    data class Error(val msg: String) : CounselingSummaryUiState
}