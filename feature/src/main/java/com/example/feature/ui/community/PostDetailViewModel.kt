package com.example.feature.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.community.CommunityRepository
import com.example.core.ui.component.DialogData
import com.example.core.util.extension.toFormattedDate
import com.example.model.community.DetailComment
import com.example.model.community.DetailRequest
import com.example.model.community.DetailResponse
import com.example.model.community.Post
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

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

    init {
        getContent()
    }

    private fun getContent() {
        viewModelScope.launch {
            communityRepository.getCommunityPostDetail(
                request = request.copy(
                    page = 0, size = 10
                )
            ).fold(
                onSuccess = {
                    _post.value = it
                    _commentList.value = it.commentList
                },
                onFailure = {
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
                    getContent()
                },
                onFailure = {
                    // TODO
                }
            )
        }
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