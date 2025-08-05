package com.example.feature.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.community.CommunityRepository
import com.example.model.community.CommunityRequest
import com.example.model.community.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _problemPosts = MutableStateFlow<List<Post>>(emptyList())
    val problemPosts: StateFlow<List<Post>> = _problemPosts.asStateFlow()

    private val _nationalPosts = MutableStateFlow<List<Post>>(emptyList())
    val nationalPosts: StateFlow<List<Post>> = _nationalPosts.asStateFlow()

    private val _medicalPosts = MutableStateFlow<List<Post>>(emptyList())
    val medicalPosts: StateFlow<List<Post>> = _medicalPosts.asStateFlow()

    private val _etcPosts = MutableStateFlow<List<Post>>(emptyList())
    val etcPosts: StateFlow<List<Post>> = _etcPosts.asStateFlow()

    // 각 탭별 페이지 정보 저장
    private val currentPages = mutableMapOf(
        0 to 0,
        1 to 0,
        2 to 0,
        3 to 0
    )

    // 더 이상 로드할 데이터가 없는지 확인
    private val hasMoreData = mutableMapOf(
        0 to true,
        1 to true,
        2 to true,
        3 to true
    )

    init {
        loadMorePosts(0)
    }

    fun changeTab(tab: Int) {
        _selectedTab.value = tab

        if (getCurrentPostsForTab(tab).isEmpty()) {
            loadMorePosts(tab)
        }
    }

    fun loadMorePosts(categoryId: Int, page: Int? = null) {
        if (page != null) {
            hasMoreData[categoryId] = true
            currentPages[categoryId] = page
            when (categoryId) {
                0 -> _problemPosts.value = emptyList()
                1 -> _nationalPosts.value = emptyList()
                2 -> _medicalPosts.value = emptyList()
                3 -> _etcPosts.value = emptyList()
            }
        }

        if (_isLoading.value || hasMoreData[categoryId] == false) return

        _isLoading.value = true

        viewModelScope.launch {
            val currentPage = currentPages[categoryId] ?: 0
            communityRepository.getCommunityPostList(CommunityRequest(
                page = currentPage,
                size = 10,
                categoryId = categoryId.toLong()
            )).fold(
                onSuccess = {
                    when (it.postList.size) {
                        10 -> {
                            val currentPosts = getCurrentPostsForTab(categoryId)
                            val updatedPosts = currentPosts + it.postList
                            updatePostsForTab(categoryId, updatedPosts)
                            currentPages[categoryId] = currentPage + 1
                        }
                        0 -> {
                            hasMoreData[categoryId] = false
                        }
                        else -> {
                            val currentPosts = getCurrentPostsForTab(categoryId)
                            val updatedPosts = currentPosts + it.postList
                            updatePostsForTab(categoryId, updatedPosts)
                            currentPages[categoryId] = currentPage + 1

                            hasMoreData[categoryId] = false
                        }
                    }
                    _isLoading.value = false
                },
                onFailure = {
                    // TODO
                    _isLoading.value = false
                }
            )
        }
    }

    private fun getCurrentPostsForTab(categoryId: Int): List<Post> {
        return when (categoryId) {
            0 -> _problemPosts.value
            1 -> _nationalPosts.value
            2 -> _medicalPosts.value
            3 -> _etcPosts.value
            else -> emptyList()
        }
    }

    private fun updatePostsForTab(categoryId: Int, posts: List<Post>) {
        when (categoryId) {
            0 -> _problemPosts.value = posts
            1 -> _nationalPosts.value = posts
            2 -> _medicalPosts.value = posts
            3 -> _etcPosts.value = posts
        }
    }
}