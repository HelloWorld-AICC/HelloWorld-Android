package com.example.feature.ui.community.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.feature.ui.community.Community
import com.example.feature.ui.community.CommunityPostDetail
import com.example.feature.ui.community.CommunityPostWrite
import kotlinx.serialization.Serializable

//@Serializable data class Community(val id: Int)
@Serializable data object Community

@Serializable data object CommunityPostDetail

@Serializable data object CommunityPostWrite

//fun NavController.navigateToCommunity(navOptions: NavOptions? = null) = navigate(route = Community, navOptions)
fun NavController.navigateToCommunity(navOptions: NavOptions? = null) = navigate(route = "커뮤니티", navOptions)

fun NavGraphBuilder.communityScreen(
    onWriteClick: () -> Unit,
    onPostClick: () -> Unit,
) {
//    composable<Community> {
    composable("커뮤니티") {
        Community(
            onWriteClick = onWriteClick,
            onPostClick = onPostClick,
        )
    }
}

fun NavController.navigateToCommunityPostDetail(navOptions: NavOptions? = null) = navigate(route = CommunityPostDetail, navOptions)

fun NavGraphBuilder.communityPostDetailScreen(

) {
    composable<CommunityPostDetail> {
        CommunityPostDetail()
    }
}

fun NavController.navigateToCommunityPostWrite(navOptions: NavOptions? = null) = navigate(route = CommunityPostWrite, navOptions)

fun NavGraphBuilder.communityPostWriteScreen(

) {
    composable<CommunityPostWrite> {
        CommunityPostWrite()
    }
}