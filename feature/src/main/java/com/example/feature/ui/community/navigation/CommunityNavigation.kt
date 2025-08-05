package com.example.feature.ui.community.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.feature.ui.community.Community
import com.example.feature.ui.community.CommunityPostDetail
import com.example.feature.ui.community.CommunityPostWrite
import com.example.feature.ui.community.PostDetailViewModel
import com.example.feature.ui.community.PostWriteViewModel
import com.example.model.community.DetailRequest
import kotlinx.serialization.Serializable

@Serializable data object Community

@Serializable data class CommunityPostDetail(val categoryId: Int, val communityId: Int)

@Serializable data class CommunityPostWrite(val category: Int)

fun NavController.navigateToCommunity(navOptions: NavOptions? = null) = navigate(route = "커뮤니티", navOptions)

fun NavGraphBuilder.communityScreen(
    onNavigateToCommunityPostWrite: (Int) -> Unit,
    onNavigateToCommunityPostDetail: (Int, Int) -> Unit,
    onCheckCommunityUpdate: () -> Boolean,
    onClearCommunityUpdate: () -> Unit,
) {
    composable("커뮤니티") {
        Community(
            onNavigateToCommunityPostWrite = onNavigateToCommunityPostWrite,
            onNavigateToCommunityPostDetail = onNavigateToCommunityPostDetail,
            onCheckCommunityUpdate = onCheckCommunityUpdate,
            onClearCommunityUpdate = onClearCommunityUpdate,
        )
    }
}

fun NavController.navigateToCommunityPostDetail(categoryId: Int, communityId: Int, navOptions: NavOptions? = null) = navigate(CommunityPostDetail(categoryId, communityId), navOptions)

fun NavGraphBuilder.communityPostDetailScreen(
    onNavigateBack: () -> Unit,
) {
    composable<CommunityPostDetail> { entry ->
        val route = entry.toRoute<CommunityPostDetail>()

        val request = DetailRequest(categoryId = route.categoryId.toLong(), communityId = route.communityId.toLong())

        CommunityPostDetail(
            onNavigateBack = onNavigateBack,
            viewModel = hiltViewModel<PostDetailViewModel, PostDetailViewModel.Factory>(
                key = "${request.communityId}"
            ) { factory ->
                factory.create(request)
            }
        )
    }
}

fun NavController.navigateToCommunityPostWrite(category: Int, navOptions: NavOptions? = null) = navigate(CommunityPostWrite(category), navOptions)

fun NavGraphBuilder.communityPostWriteScreen(
    onNavigateBack: () -> Unit,
    onCommunityUpdated: () -> Unit,
) {
    composable<CommunityPostWrite> { entry ->
        val category = entry.toRoute<CommunityPostWrite>().category

        CommunityPostWrite(
            onNavigateBack = onNavigateBack,
            onCommunityUpdated = onCommunityUpdated,
            viewModel = hiltViewModel<PostWriteViewModel, PostWriteViewModel.Factory>(
                key = "$category"
            ) { factory ->
                factory.create(category)
            }
        )
    }
}