package com.example.feature.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.UserInfoUseCase
import com.example.model.common.Result
import com.example.model.mypage.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userInfoUseCase: UserInfoUseCase
) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    fun fetchUserInfoIfTokenExists() {
        Log.d("HomeViewModel", "fetchUserInfoIfTokenExists() called")

        viewModelScope.launch {
            Log.d("HomeViewModel", "Calling userInfoUseCase()...")
            userInfoUseCase().collect {
                when (it) {
                    is Result.Success -> {
                        Log.d("HomeViewModel", "User info fetch success: ${it.data}")
                        _userInfo.value = it.data
                    }
                    is Result.Error -> {
                        Log.e("HomeViewModel", "Error fetching user info: ${it.exception}")
                    }
                    is Result.Loading -> {
                        Log.d("HomeViewModel", "Loading user info...")
                    }
                }
            }
        }
    }
}
