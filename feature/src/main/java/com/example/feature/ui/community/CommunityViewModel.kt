package com.example.feature.ui.community

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(

) : ViewModel() {

    private val _selectedTab = MutableStateFlow<String>("problem")
    val selectedTab: StateFlow<String> = _selectedTab

    private val _problemPosts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val problemPosts: StateFlow<List<CommunityPost>> = _problemPosts

    private val _nationalPosts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val nationalPosts: StateFlow<List<CommunityPost>> = _nationalPosts

    private val _medicalPosts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val medicalPosts: StateFlow<List<CommunityPost>> = _medicalPosts

    private val _etcPosts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val etcPosts: StateFlow<List<CommunityPost>> = _etcPosts

//    val currentTabPosts = _selectedTab.flatMapLatest { tab ->
//        when (tab) {
//            "problem" -> problemPosts
//            "national" -> nationalPosts
//            "medical" -> medicalPosts
//            "etc" -> etcPosts
//            else -> flowOf(emptyList())
//        }
//    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
//
//    init {
//        loadData()
//    }

    fun changeTab(tab: String) {
        _selectedTab.value = tab
    }

//    private fun loadData() {
//        // 각 탭별 데이터 로드 (레포지토리에서 데이터 가져오기)
//        viewModelScope.launch {
//            _problemPosts.value = repository.getProblemPosts()
//            _nationalPosts.value = repository.getNationalPosts()
//            _medicalPosts.value = repository.getMedicalPosts()
//            _etcPosts.value = repository.getEtcPosts()
//        }
//    }
}