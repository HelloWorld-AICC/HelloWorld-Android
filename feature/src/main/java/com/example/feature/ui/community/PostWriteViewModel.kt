package com.example.feature.ui.community

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.community.CommunityRepository
import com.example.core.ui.component.DialogData
import com.example.feature.ui.community.navigation.CommunityPostWrite
import com.example.model.common.ContentType
import com.example.model.community.DetailRequest
import com.example.model.community.UpdatePostRequest
import com.example.model.community.WriteFileRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = PostWriteViewModel.Factory::class)
class PostWriteViewModel @AssistedInject constructor(
    private val communityRepository: CommunityRepository,
    @Assisted val request: CommunityPostWrite,
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(request.category)
    val selectedTab: StateFlow<Int> = _selectedTab

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content

    private val _dialogData = MutableStateFlow<DialogData?>(null)
    val dialogData: StateFlow<DialogData?> = _dialogData

    fun changeTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun updateContent(newContent: String) {
        _content.value = newContent
    }

    fun updateDialogData(data: DialogData? = null) {
        _dialogData.value = data
    }

    private val _images = MutableStateFlow<List<Uri>>(emptyList())
    val images: StateFlow<List<Uri>> = _images.asStateFlow()
    private val maxImageCount = 10

    private val _editingUserImg = MutableStateFlow<Uri?>(null)
    val editingUserImg: StateFlow<Uri?> = _editingUserImg.asStateFlow()

    fun addImages(uris: List<Uri>) {
        val currentImages = _images.value
        val remainingSlots = maxImageCount - currentImages.size

        if (remainingSlots > 0) {
            val imagesToAdd = uris.take(remainingSlots)
            _images.value = currentImages + imagesToAdd
        }
    }

    fun removeImage(uri: Uri) {
        _images.value = _images.value.filter { it != uri }
    }

    init {
        if (request.type == ContentType.UPDATE) {
            getContent()
        }
    }

    fun getContent() {
        viewModelScope.launch {
            communityRepository.getCommunityPostDetail(
                request = DetailRequest(
                    communityId = request.communityId.toLong()
                )
            ).fold(
                onSuccess = {
                    _title.value = it.title
                    _content.value = it.content
                },
                onFailure = {
                    // TODO
                }
            )
        }
    }

    fun submitCommunityPost(context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {

            val mediaBytesList = _images.value.mapNotNull { uri ->
                try {
                    val contentResolver = context.contentResolver
                    val mimeType = contentResolver.getType(uri)
                    val bytes = contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.readBytes()
                    }
                    bytes?.let { WriteFileRequest(it, mimeType) }
                } catch (e: Exception) {
                    WriteFileRequest(byteArrayOf(), "")
                }
            }

            communityRepository.submitCommunityPost(
                category = _selectedTab.value.toLong(),
                title = _title.value,
                content = _content.value,
                images = mediaBytesList
            ).fold(
                onSuccess = {
                    onResult(true)
                },
                onFailure = {
                    // TODO
                }
            )
        }
    }

    fun updateCommunityPost(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            communityRepository.updatePost(
                communityId = request.communityId.toLong(),
                request = UpdatePostRequest(
                    title = _title.value,
                    content = _content.value,
                    communityCategoryId = _selectedTab.value
                )
            ).fold(
                onSuccess = {
                    onResult(true)
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
            request: CommunityPostWrite
        ): PostWriteViewModel
    }

}