package com.example.feature.ui.community.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.feature.ui.community.Community
import com.example.feature.ui.community.CommunityPostDetail
import com.example.feature.ui.community.CommunityPostWrite
import kotlinx.serialization.Serializable

@Serializable data object Community

//@Serializable data class CommunityPostDetail(val id: Int)
@Serializable data object CommunityPostDetail

@Serializable data object CommunityPostWrite

//fun NavController.navigateToCommunity(navOptions: NavOptions? = null) = navigate(route = Community, navOptions)
fun NavController.navigateToCommunity(navOptions: NavOptions? = null) = navigate(route = "커뮤니티", navOptions)

fun NavGraphBuilder.communityScreen(
    onNavigateToCommunityPostWrite: () -> Unit,
    onNavigateToCommunityPostDetail: () -> Unit,
) {
//    composable<Community> {
    composable("커뮤니티") {
        Community(
            onNavigateToCommunityPostWrite = onNavigateToCommunityPostWrite,
            onNavigateToCommunityPostDetail = onNavigateToCommunityPostDetail,
        )
    }
}

fun NavController.navigateToCommunityPostDetail(navOptions: NavOptions? = null) = navigate(route = CommunityPostDetail, navOptions)

fun NavGraphBuilder.communityPostDetailScreen(
    onNavigateBack: () -> Unit,
) {
    composable<CommunityPostDetail> {
        CommunityPostDetail(
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToCommunityPostWrite(navOptions: NavOptions? = null) = navigate(route = CommunityPostWrite, navOptions)

fun NavGraphBuilder.communityPostWriteScreen(
    onNavigateBack: () -> Unit,
) {
    composable<CommunityPostWrite> {
        CommunityPostWrite(
            onNavigateBack = onNavigateBack
        )
    }
}