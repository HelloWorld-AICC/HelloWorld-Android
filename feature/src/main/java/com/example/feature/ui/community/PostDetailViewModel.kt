package com.example.feature.ui.community

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.community.CommunityRepository
import com.example.core.ui.component.DialogData
import com.example.core.ui.component.ToastData
import com.example.model.community.DetailComment
import com.example.model.community.DetailRequest
import com.example.model.community.DetailResponse
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PostDetailViewModel.Factory::class)
class PostDetailViewModel @AssistedInject constructor(
    private val communityRepository: CommunityRepository,
    @Assisted val request: DetailRequest,
) : ViewModel() {

    private val _showMediaViewer = MutableStateFlow(MediaViewData())
    val showMediaViewer: StateFlow<MediaViewData> = _showMediaViewer.asStateFlow()

    fun visibleMediaViewer(data: MediaViewData) {
        _showMediaViewer.value = data
    }

    private val _post = MutableStateFlow(DetailResponse())
    val post: StateFlow<DetailResponse> = _post.asStateFlow()

    private val _commentList = MutableStateFlow<List<DetailComment>>(emptyList())
    val commentList: StateFlow<List<DetailComment>> = _commentList.asStateFlow()

    private val _toastData = MutableStateFlow<ToastData?>(null)
    val toastData = _toastData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _hasMoreData = MutableStateFlow(true)
    private val _page = MutableStateFlow(0)

    init {
        getContent(true)
    }

    fun getContent(init: Boolean? = null) {
        if (init == true) {
            _post.update { DetailResponse() }
            _commentList.value = emptyList()
            _page.value = 0
        }

        if (init != true && (_isLoading.value || !_hasMoreData.value)) return

        if (_commentList.value.isNotEmpty()) _isLoading.value = true

        viewModelScope.launch {
            communityRepository.getCommunityPostDetail(
                request = request.copy(
                    page = _page.value,
                    size = 10,
                )
            ).fold(
                onSuccess = {
                    _post.value = it

                    _commentList.value += it.commentList
                    _hasMoreData.value = it.commentList.size == 10
                    _page.value++

                    _isLoading.value = false
                },
                onFailure = {
                    _isLoading.value = false
                    // TODO
                }
            )
        }
    }

    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText

    private val _dialogData = MutableStateFlow<DialogData?>(null)
    val dialogData: StateFlow<DialogData?> = _dialogData

    fun updateComment(comment: String) {
        _commentText.value = comment
    }

    fun updateDialogData(data: DialogData? = null) {
        _dialogData.value = data
    }

    fun submitComment() {
        viewModelScope.launch {
            communityRepository.submitComment(
                communityId = request.communityId,
                content = _commentText.value
            ).fold(
                onSuccess = {
                    _commentText.value = ""
                    getContent(true)
                },
                onFailure = {
                    // TODO
                }
            )
        }
    }

    fun deletePost(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            communityRepository.deletePost(
                categoryId = _post.value.categoryId,
                communityId = request.communityId
            ).fold(
                onSuccess = {
                    onResult(true)
                },
                onFailure = {
                    onResult(false)
                }
            )
        }
    }

    fun deleteComment(commentId: Long = 0, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            communityRepository.deleteComment(
                communityId = request.communityId,
                commentId = commentId
            ).fold(
                onSuccess = { result ->
                    onResult(true)
                    _commentList.value = _commentList.value.filterNot { it.commentId == commentId}
                },
                onFailure = {
                    onResult(false)
                }
            )
        }
    }

    /** 게시글 신고하기 */
    fun reportPost(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            communityRepository.reportPost(
                request.communityId
            ).fold(
                onSuccess = {
                    onResult(true)
                },
                onFailure = {
                    onResult(false)
                }
            )
        }
    }

    fun reportComment() {

    }

    fun updateToastData(data: ToastData? = null) {
        _toastData.value = data
    }

    @AssistedFactory
    interface Factory {
        fun create(
            request: DetailRequest
        ): PostDetailViewModel
    }
}

data class MediaViewData(
    val visible: Boolean = false,
    val pageNum: Int = 0,
)