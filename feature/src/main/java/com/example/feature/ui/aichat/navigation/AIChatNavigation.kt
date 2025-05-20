package com.example.feature.ui.aichat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.feature.ui.aichat.AiChatScreen
import com.example.feature.ui.aichat.chatting.RecentChattingScreen
import kotlinx.serialization.Serializable

@Serializable
data object ChattingScreen

fun NavGraphBuilder.aiChatScreen(
    onPostClick: () -> Unit,
) {
    composable("채팅 상담") {
        AiChatScreen(
            onPostClick = onPostClick,
        )
    }
}

fun NavController.navigateToAIChatDetail(navOptions: NavOptions? = null) = navigate(route = ChattingScreen, navOptions)

fun NavGraphBuilder.aiChatDetailScreen(
    onBackClick: () -> Unit

) {
    composable<ChattingScreen> {
        RecentChattingScreen(
            onBackClick = onBackClick
        )
    }
}

