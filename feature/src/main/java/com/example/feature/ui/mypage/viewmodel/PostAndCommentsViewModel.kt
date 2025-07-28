package com.example.feature.ui.mypage.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.mypage.MyPageRepository
import com.example.model.mypage.AllCommentResponse
import com.example.model.mypage.AllCommunityResponse
import com.example.model.mypage.Comment
import com.example.model.mypage.Community
import com.example.model.mypage.PageSizeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostAndCommentsViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiState = MutableStateFlow<MyPostAndCommentUiState>(MyPostAndCommentUiState.Loading)
    val uiState: StateFlow<MyPostAndCommentUiState> = _uiState.asStateFlow()

    private val _selectedMenu = MutableStateFlow("게시글")
    val selectedMenu: StateFlow<String> = _selectedMenu.asStateFlow()

    private val _posts = MutableStateFlow<List<Community>>(emptyList())
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())

    private var currentPages = mutableIntStateOf(0)
    private var hasMoreData = mutableStateOf(true)

    init {
        loadData()
    }

    fun changeMenu(menu: String) {
        _selectedMenu.value = menu
        _posts.value = emptyList()
        _comments.value = emptyList()

        currentPages.intValue = 0
        hasMoreData.value = true
        loadData(menu)
    }

    fun loadData(type: String = "게시글") {
        if (_isLoading.value || !hasMoreData.value) return

        _isLoading.value = true

        viewModelScope.launch {
            MyPostAndCommentUiState.Loading
            when (type) {
                "게시글" -> {
                    myPageRepository.getAllMyCommunity(
                        PageSizeRequest(
                            page = currentPages.intValue,
                            size = 10,
                        )
                    ).fold(
                        onSuccess = {
                            when (it.allMyCommunityList.size) {
                                10 -> {
                                    _posts.value += it.allMyCommunityList
                                    currentPages.intValue += 1
                                }
                                0 -> {
                                    hasMoreData.value = false
                                }
                                else -> {
                                    _posts.value += it.allMyCommunityList
                                    hasMoreData.value = false
                                }
                            }
                            _isLoading.value = false
                            _uiState.value = MyPostAndCommentUiState.Success(_posts.value)
                        },
                        onFailure = {
                            _isLoading.value = false
                            _uiState.value = MyPostAndCommentUiState.Error("${it.message}")
                        }
                    )
                }
                "댓글" -> {
                    myPageRepository.getAllMyComment(
                        PageSizeRequest(
                            page = currentPages.intValue,
                            size = 10,
                        )
                    ).fold(
                        onSuccess = {
                            when (it.allMyCommentList.size) {
                                10 -> {
                                    _comments.value += it.allMyCommentList
                                    currentPages.intValue += 1
                                }
                                0 -> {
                                    hasMoreData.value = false
                                }
                                else -> {
                                    _comments.value += it.allMyCommentList
                                    hasMoreData.value = false
                                }
                            }
                            _isLoading.value = false
                            _uiState.value = MyPostAndCommentUiState.Success(_comments.value)
                        },
                        onFailure = {
                            _isLoading.value = false
                            _uiState.value = MyPostAndCommentUiState.Error("${it.message}")
                        }
                    )
                }
            }
        }
    }

}

sealed interface MyPostAndCommentUiState {
    data object Loading: MyPostAndCommentUiState
    data class Success<T>(val result: List<T>): MyPostAndCommentUiState
    data class Error(val msg: String): MyPostAndCommentUiState
}