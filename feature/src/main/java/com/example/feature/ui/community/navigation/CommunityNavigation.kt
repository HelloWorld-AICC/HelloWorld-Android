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
import com.example.model.common.ContentType
import com.example.model.community.DetailRequest
import kotlinx.serialization.Serializable

@Serializable data object Community

@Serializable data class CommunityPostDetail(val communityId: Int)

@Serializable data class CommunityPostWrite(val category: Int, val communityId: Int = 0, val type: ContentType)

fun NavController.navigateToCommunity(navOptions: NavOptions? = null) = navigate(route = "커뮤니티", navOptions)

fun NavGraphBuilder.communityScreen(
    onNavigateToCommunityPostWrite: (Int, Int, ContentType) -> Unit,
    onNavigateToCommunityPostDetail: (Int) -> Unit,
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

fun NavController.navigateToCommunityPostDetail(communityId: Int, navOptions: NavOptions? = null) = navigate(CommunityPostDetail(communityId), navOptions)

fun NavGraphBuilder.communityPostDetailScreen(
    onNavigateToCommunityPostWrite: (Int, Int, ContentType) -> Unit,
    onNavigateBack: () -> Unit,
    onCommunityUpdated: () -> Unit,
    onCheckCommunityUpdate: () -> Boolean,
) {
    composable<CommunityPostDetail> { entry ->
        val route = entry.toRoute<CommunityPostDetail>()

        val request = DetailRequest(communityId = route.communityId.toLong())

        CommunityPostDetail(
            onNavigateToCommunityPostWrite = onNavigateToCommunityPostWrite,
            onNavigateBack = onNavigateBack,
            onCommunityUpdated = onCommunityUpdated,
            onCheckCommunityUpdate = onCheckCommunityUpdate,
            viewModel = hiltViewModel<PostDetailViewModel, PostDetailViewModel.Factory>(
                key = "${request.communityId}"
            ) { factory ->
                factory.create(request)
            }
        )
    }
}

fun NavController.navigateToCommunityPostWrite(
    category: Int,
    communityId: Int = 0,
    contentType: ContentType,
    navOptions: NavOptions? = null
) = navigate(CommunityPostWrite(category, communityId, contentType), navOptions)

fun NavGraphBuilder.communityPostWriteScreen(
    onNavigateBack: () -> Unit,
    onCommunityUpdated: () -> Unit,
) {
    composable<CommunityPostWrite> { entry ->
        val route = entry.toRoute<CommunityPostWrite>()

        val request = CommunityPostWrite(
            route.category,
            route.communityId,
            route.type
        )

        CommunityPostWrite(
            onNavigateBack = onNavigateBack,
            onCommunityUpdated = onCommunityUpdated,
            viewModel = hiltViewModel<PostWriteViewModel, PostWriteViewModel.Factory>(
                key = "${route.category} ${route.communityId}"
            ) { factory ->
                factory.create(request)
            }
        )
    }
}