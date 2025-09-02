package com.example.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.feature.onboarding.AgreementScreen
import com.example.feature.ui.aichat.navigation.aiChatDetailScreen
import com.example.feature.ui.aichat.navigation.aiChatScreen
import com.example.feature.ui.aichat.navigation.navigateToAIChatDetail
import com.example.feature.ui.community.navigation.communityPostDetailScreen
import com.example.feature.ui.community.navigation.communityPostWriteScreen
import com.example.feature.ui.community.navigation.communityScreen
import com.example.feature.ui.community.navigation.navigateToCommunityPostDetail
import com.example.feature.ui.community.navigation.navigateToCommunityPostWrite
import com.example.feature.ui.consultationCenter.ConsultationCenterScreen
import com.example.feature.ui.home.screen.HomeScreen
import com.example.feature.ui.mypage.navigation.counselingDetailScreen
import com.example.feature.ui.mypage.navigation.counselingSummaryScreen
import com.example.feature.ui.mypage.navigation.myPageScreen
import com.example.feature.ui.mypage.navigation.navigateToCounselingDetail
import com.example.feature.ui.mypage.navigation.navigateToCounselingSummary
import com.example.feature.ui.mypage.navigation.navigateToPostAndComments
import com.example.feature.ui.mypage.navigation.navigateToPrivacyPolicy
import com.example.feature.ui.mypage.navigation.navigateToProfileEdit
import com.example.feature.ui.mypage.navigation.navigateToResume
import com.example.feature.ui.mypage.navigation.navigateToTermsOfService
import com.example.feature.ui.mypage.navigation.navigateToWithdraw
import com.example.feature.ui.mypage.navigation.navigateToWithdrawComplete
import com.example.feature.ui.mypage.navigation.postAndCommentsScreen
import com.example.feature.ui.mypage.navigation.privacyPolicyScreen
import com.example.feature.ui.mypage.navigation.profileEditScreen
import com.example.feature.ui.mypage.navigation.resumeScreen
import com.example.feature.ui.mypage.navigation.termsOfServiceScreen
import com.example.feature.ui.mypage.navigation.withdrawCompleteScreen
import com.example.feature.ui.mypage.navigation.withdrawScreen
import com.example.feature.ui.onboarding.screen.CongratulationsScreen
import com.example.feature.ui.onboarding.screen.LanguageScreen
import com.example.feature.ui.onboarding.screen.LoginScreen
import com.example.feature.ui.splash.screen.SplashScreen

// 아이콘 리소스 구성 (기본 / 클릭)
data class NavItem(
    val route: String,
    val iconResId: Int,
    val iconClickResId: Int
)

// 아이콘 리스트 구성
val items = listOf(
    NavItem("상담 센터", R.drawable.ic_consultation_center, R.drawable.ic_consultation_center_click),
    NavItem("채팅 상담", R.drawable.ic_chat_consultation2, R.drawable.ic_chat_consultation2_click),
    NavItem("홈", R.drawable.ic_home, R.drawable.ic_home_click),
    NavItem("이력서 작성", R.drawable.ic_resume_writing, R.drawable.ic_resume_writing_click),
    NavItem("커뮤니티", R.drawable.ic_community, R.drawable.ic_community_click)
)

@Composable
fun MyBottomNavigation(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.route == item.route
            val iconRes = if (isSelected) item.iconClickResId else item.iconResId

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = item.route,
                        tint = Color.Unspecified // 아이콘 색상 유지
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

// 메인 네비게이션 호스트
@Composable
fun MyNavigationHost(navController: NavHostController) {
    NavHost(navController, startDestination = "스플래시") {
        composable("스플래시") { SplashScreen(navController) }
        composable("온보딩") { LoginScreen(navController) }
        composable("언어 설정") { LanguageScreen(navController) }
        composable("이용 동의") { AgreementScreen(navController) }

        composable("축하") { CongratulationsScreen(navController) }
        composable("홈") { HomeScreen(navController) } // TODO 마이페이지로 이동 경로 수정 해주세요
        composable("상담 센터") {
            ConsultationCenterScreen (
                onBackClick = navController::navigateUp
            )
        }
        aiChatScreen(
            onPostClick = { chatId ->
                navController.navigateToAIChatDetail(chatId!!)
            }
        )
        aiChatDetailScreen(
            onBackClick = navController::navigateUp
        )

        composable("이력서 작성") { Text("이력서 작성 화면", modifier = Modifier.padding(16.dp)) }
        communityScreen(
            onNavigateToCommunityPostWrite = navController::navigateToCommunityPostWrite,
            onNavigateToCommunityPostDetail = navController::navigateToCommunityPostDetail,
            onCheckCommunityUpdate = { navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>("community_update") ?: false },
            onClearCommunityUpdate = { navController.previousBackStackEntry?.savedStateHandle?.set("community_update", false) }
        )
        communityPostDetailScreen(
            onNavigateToCommunityPostWrite = navController::navigateToCommunityPostWrite,
            onNavigateBack = navController::navigateUp,
            onCommunityUpdated = { navController.previousBackStackEntry?.savedStateHandle?.set("community_update", true) },
            onCheckCommunityUpdate = { navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>("community_update") ?: false },

        )
        communityPostWriteScreen(
            onNavigateBack = navController::navigateUp,
            onCommunityUpdated = {
                navController.previousBackStackEntry?.savedStateHandle?.set("community_update", true)
                navController.popBackStack()
            }
        )
        myPageScreen(
            onNavigateBack = navController::navigateUp,
            onNavigateToProfileEdit = navController::navigateToProfileEdit,
            onNavigateToCounselingSummary = navController::navigateToCounselingSummary,
            onNavigateToResume = navController::navigateToResume,
            onNavigateToPostAndComments = navController::navigateToPostAndComments,
            onNavigateToTermsOfService = navController::navigateToTermsOfService,
            onNavigateToPrivacyPolicy = navController::navigateToPrivacyPolicy,
            onNavigateToLogin = {
                val navOption = navOptions {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
                navController.navigate("온보딩", navOption)
            },
            onNavigateToWithdraw = navController::navigateToWithdraw,
            onCheckProfileUpdate = { navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>("profile_updated") ?: false },
            onClearProfileUpdate = { navController.previousBackStackEntry?.savedStateHandle?.set("profile_updated", false) }
        )
        profileEditScreen(
            onNavigateBack = navController::navigateUp,
            onProfileUpdated = {
                navController.previousBackStackEntry?.savedStateHandle?.set("profile_updated", true)
                navController.popBackStack()
            }
        )
        counselingSummaryScreen(
            onNavigateBack = navController::navigateUp,
            onNavigateToCounselingDetail = navController::navigateToCounselingDetail
        )
        counselingDetailScreen(
            onNavigateBack = navController::navigateUp,
            onNavigateToAIChatDetail = { roomId -> navController.navigateToAIChatDetail(roomId.toString()) }
        )
        resumeScreen(
            onNavigateBack = navController::navigateUp
        )
        postAndCommentsScreen(
            onNavigateBack = navController::navigateUp,
            onNavigateCommunity = navController::navigateToCommunityPostDetail,
        )
        privacyPolicyScreen( onNavigateBack = navController::navigateUp )
        termsOfServiceScreen( onNavigateBack = navController::navigateUp )
        withdrawScreen(
            onNavigateBack = navController::navigateUp,
            onNavigateToWithdrawComplete = {
                navController.navigateToWithdrawComplete(
                    navOptions = navOptions {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                )
            },
        )
        withdrawCompleteScreen( onNavigateToLogin = {
            val navOption = navOptions {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
            navController.navigate("온보딩", navOption)
        } )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 바텀바가 보일 화면만 정의
    val bottomBarRoutes = items.map { it.route }

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                MyBottomNavigation(navController)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MyNavigationHost(navController)
        }
    }
}
