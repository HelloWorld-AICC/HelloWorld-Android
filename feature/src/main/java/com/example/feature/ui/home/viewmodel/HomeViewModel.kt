package com.example.feature.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import com.example.feature.ui.mypage.viewmodel.MyPageViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// Hilt이 ViewModel 관리 (다른 Composable에서 @HiltViewModel로 쉽게 불러옴)
@HiltViewModel

// @Inject constructor: 생성자에 필요한 의존성 자동 주입
class HomeViewModel @Inject constructor(
    
    // myPageViewModel: 사용자 정보 관련 로직 중복 구현 없이 가져다 씀
    private val myPageViewModel: MyPageViewModel
) : ViewModel() {

    // userIndfo: MyPageViewModel에서 관리하는 LiveData
    val userInfo = myPageViewModel.userInfo

    // 토큰 유효 시 사용자 정보 비동기로 조회
    fun loadUserInfoIfNeeded(token: String) {
        if(token.isNotBlank()) {
            myPageViewModel.fetchUserInfoIfTokenExists()
        }
    }
}