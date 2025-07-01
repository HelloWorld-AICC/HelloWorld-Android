package com.example.feature.ui.mypage.viewmodel

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

    private val _uiState = MutableStateFlow<MyPostAndCommentUiState>(MyPostAndCommentUiState.Loading)
    val uiState: StateFlow<MyPostAndCommentUiState> = _uiState.asStateFlow()

    private val _selectedMenu = MutableStateFlow<String>("게시글")
    val selectedMenu: StateFlow<String> = _selectedMenu.asStateFlow()

    private val _posts = MutableStateFlow<List<Community>>(emptyList())
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())

    init {
        loadData()
    }

    fun changeMenu(menu: String) {
        _selectedMenu.value = menu
        _posts.value = emptyList()
        _comments.value = emptyList()
        loadData(menu)
    }

    fun loadData(type: String = "게시글", page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            MyPostAndCommentUiState.Loading
            when (type) {
                "게시글" -> {
                    myPageRepository.getAllMyCommunity(
                        PageSizeRequest(
                            page = page,
                            size = size,
                        )
                    ).fold(
                        onSuccess = {
                            _posts.value += it.allMyCommunityList
                            _uiState.value = MyPostAndCommentUiState.Success(_posts.value)
                        },
                        onFailure = {
                            _uiState.value = MyPostAndCommentUiState.Error("${it.message}")
                        }
                    )
                }
                "댓글" -> {
                    myPageRepository.getAllMyComment(
                        PageSizeRequest(
                            page = page,
                            size = size,
                        )
                    ).fold(
                        onSuccess = {
                            _comments.value += it.allMyCommentList
                            _uiState.value = MyPostAndCommentUiState.Success(_comments.value)
                        },
                        onFailure = {
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