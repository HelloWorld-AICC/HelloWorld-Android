package com.example.feature.ui.community

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.feature.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    @ApplicationContext private val context: Context
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

    init {
        loadData(context)
    }

    fun changeTab(tab: String) {
        _selectedTab.value = tab
    }

    private fun loadData(context: Context) {
        val inputStream = context.resources.openRawResource(R.raw.dummy_community)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        val gson = Gson()
        val listType = object : TypeToken<List<CommunityPost>>() {}.type
        val allPosts: List<CommunityPost> = gson.fromJson(jsonString, listType)

        _problemPosts.value = allPosts.filter { it.category == "problem" }
        _nationalPosts.value = allPosts.filter { it.category == "national" }
        _medicalPosts.value = allPosts.filter { it.category == "medical" }
        _etcPosts.value = allPosts.filter { it.category == "etc" }
    }
}