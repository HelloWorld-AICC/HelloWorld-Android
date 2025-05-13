package com.example.core.ui.navigation

import com.example.core.ui.R

data class NavItem(
    val route: String,
    val iconResId: Int,
    val iconClickResId: Int
)

val bottomNavItems = listOf(
    NavItem("상담 센터", R.drawable.ic_consultation_center, R.drawable.ic_consultation_center_click),
    NavItem("채팅 상담", R.drawable.ic_chat_consultation2, R.drawable.ic_chat_consultation2_click),
    NavItem("홈", R.drawable.ic_home, R.drawable.ic_home_click),
    NavItem("이력서 작성", R.drawable.ic_resume_writing, R.drawable.ic_resume_writing_click),
    NavItem("커뮤니티", R.drawable.ic_community, R.drawable.ic_community_click)
)
