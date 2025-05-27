package com.example.feature.ui.mypage.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.feature.ui.mypage.screen.CounselingDetail
import com.example.feature.ui.mypage.screen.CounselingSummary
import com.example.feature.ui.mypage.screen.MyPage
import com.example.feature.ui.mypage.screen.PostAndComments
import com.example.feature.ui.mypage.screen.PrivacyPolicy
import com.example.feature.ui.mypage.screen.ProfileEdit
import com.example.feature.ui.mypage.screen.Resume
import com.example.feature.ui.mypage.screen.TermsOfService
import com.example.feature.ui.mypage.screen.Withdraw
import com.example.feature.ui.mypage.screen.WithdrawComplete
import kotlinx.serialization.Serializable

@Serializable data object MyPage

@Serializable data object ProfileEdit

@Serializable data object CounselingSummary

@Serializable data object CounselingDetail

@Serializable data object Resume

@Serializable data object PostAndComments

@Serializable data object PrivacyPolicy

@Serializable data object TermsOfService

@Serializable data object Withdraw

@Serializable data object WithdrawComplete

fun NavController.navigateToMyPage(navOptions: NavOptions? = null) = navigate(MyPage, navOptions)

fun NavGraphBuilder.myPageScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToCounselingSummary: () -> Unit,
    onNavigateToResume: () -> Unit,
    onNavigateToPostAndComments: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToTermsOfService: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
) {
    composable<MyPage> {
        MyPage(
            onNavigateBack = onNavigateBack,
            onNavigateToProfileEdit = onNavigateToProfileEdit,
            onNavigateToCounselingSummary = onNavigateToCounselingSummary,
            onNavigateToResume = onNavigateToResume,
            onNavigateToPostAndComments = onNavigateToPostAndComments,
            onNavigateToPrivacyPolicy = onNavigateToPrivacyPolicy,
            onNavigateToTermsOfService = onNavigateToTermsOfService,
            onNavigateToWithdraw = onNavigateToWithdraw
        )
    }
}

fun NavController.navigateToProfileEdit(navOptions: NavOptions? = null) = navigate(ProfileEdit, navOptions)

fun NavGraphBuilder.profileEditScreen(
    onNavigateBack: () -> Unit,
) {
    composable<ProfileEdit> {
        ProfileEdit(
            onNavigateBack = onNavigateBack,
        )
    }
}

fun NavController.navigateToCounselingSummary(navOptions: NavOptions? = null) = navigate(CounselingSummary, navOptions)

fun NavGraphBuilder.counselingSummaryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCounselingDetail: () -> Unit,
) {
    composable<CounselingSummary> {
        CounselingSummary(
            onNavigateBack = onNavigateBack,
            onNavigateToCounselingDetail = onNavigateToCounselingDetail
        )
    }
}

fun NavController.navigateToCounselingDetail(navOptions: NavOptions? = null) = navigate(CounselingDetail, navOptions)

fun NavGraphBuilder.counselingDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAIChatDetail: (Int) -> Unit,
) {
    composable<CounselingDetail> {
        CounselingDetail(
            onNavigateBack = onNavigateBack,
            onNavigateToAIChatDetail = onNavigateToAIChatDetail,
        )
    }
}

fun NavController.navigateToResume(navOptions: NavOptions? = null) = navigate(Resume, navOptions)

fun NavGraphBuilder.resumeScreen(
    onNavigateBack: () -> Unit,
) {
    composable<Resume> {
        Resume(
        )
    }
}

fun NavController.navigateToPostAndComments(navOptions: NavOptions? = null) = navigate(PostAndComments, navOptions)

fun NavGraphBuilder.postAndCommentsScreen(
    onNavigateBack: () -> Unit,
) {
    composable<PostAndComments> {
        PostAndComments(
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToPrivacyPolicy(navOptions: NavOptions? = null) = navigate(PrivacyPolicy, navOptions)

fun NavGraphBuilder.privacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    composable<PrivacyPolicy> {
        PrivacyPolicy(
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToTermsOfService(navOptions: NavOptions? = null) = navigate(TermsOfService, navOptions)

fun NavGraphBuilder.termsOfServiceScreen(
    onNavigateBack: () -> Unit
) {
    composable<TermsOfService> {
        TermsOfService(
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToWithdraw(navOptions: NavOptions? = null) = navigate(Withdraw, navOptions)

fun NavGraphBuilder.withdrawScreen(
    onNavigateBack: () -> Unit,
    onNavigateToWithdrawComplete: () -> Unit,
) {
    composable<Withdraw> {
        Withdraw(
            onNavigateBack = onNavigateBack,
            onNavigateToWithdrawComplete = onNavigateToWithdrawComplete,
        )
    }
}

fun NavController.navigateToWithdrawComplete(navOptions: NavOptions? = null) = navigate(WithdrawComplete, navOptions)

fun NavGraphBuilder.withdrawCompleteScreen(
    onNavigateToLogin: () -> Unit
) {
    composable<WithdrawComplete> {
        WithdrawComplete(
            onNavigateToLogin = onNavigateToLogin
        )
    }
}