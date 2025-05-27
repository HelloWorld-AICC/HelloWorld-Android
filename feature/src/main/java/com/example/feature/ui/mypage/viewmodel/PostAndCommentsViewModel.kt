package com.example.feature.ui.mypage.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.feature.R
import com.example.feature.ui.community.CommunityPost
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PostAndCommentsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _selectedMenu = MutableStateFlow<String>("게시글")
    val selectedMenu: StateFlow<String> = _selectedMenu

    private val _posts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val posts: StateFlow<List<CommunityPost>> = _posts

    init {
        loadData(context)
    }

    fun changeMenu(menu: String) {
        _selectedMenu.value = menu
    }

    private fun loadData(context: Context) {
        val inputStream = context.resources.openRawResource(R.raw.dummy_community)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        val gson = Gson()
        val listType = object : TypeToken<List<CommunityPost>>() {}.type
        val allPosts: List<CommunityPost> = gson.fromJson(jsonString, listType)

        _posts.value = allPosts.filter { it.category == "problem" }
    }

}