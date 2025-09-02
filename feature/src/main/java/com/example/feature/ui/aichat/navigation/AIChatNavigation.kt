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
    onPostClick: (String?) -> Unit,
) {
    composable("채팅 상담") {
        AiChatScreen(
            onPostClick = onPostClick,
        )
    }
}

fun NavController.navigateToAIChatDetail(roomId: String, navOptions: NavOptions? = null) {
    navigate("room/$roomId", navOptions)
}
fun NavGraphBuilder.aiChatDetailScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = "room/{roomId}",
        arguments = listOf(
            navArgument("roomId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable

        // "new"인 경우 새 채팅 로직
        if (roomId == "new_chat") {
            // 예: ViewModel에서 새로운 roomId 생성 및 상태 준비
        }

        if (roomId != null) {
            RecentChattingScreen(
                roomId = roomId,
                onBackClick = onBackClick
            )
        }
    }
}
