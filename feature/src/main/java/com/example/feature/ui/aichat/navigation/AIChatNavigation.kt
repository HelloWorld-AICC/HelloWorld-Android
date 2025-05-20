package com.example.feature.ui.aichat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.feature.ui.aichat.AiChatScreen
import com.example.feature.ui.aichat.chatting.RecentChattingScreen

fun NavGraphBuilder.aiChatScreen(
    onPostClick: (Int) -> Unit,
) {
    composable("채팅 상담") {
        AiChatScreen(
            onPostClick = onPostClick,
        )
    }
}

fun NavController.navigateToAIChatDetail(chatId: Int, navOptions: NavOptions? = null) {
    navigate("chatting/$chatId", navOptions)
}
fun NavGraphBuilder.aiChatDetailScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = "chatting/{chatId}",
        arguments = listOf(
            navArgument("chatId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val chatId = backStackEntry.arguments?.getInt("chatId") ?: -1

        RecentChattingScreen(
            chatId = chatId,
            onBackClick = onBackClick
        )
    }
}
