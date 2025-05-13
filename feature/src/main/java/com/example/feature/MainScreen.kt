package com.example.feature.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.feature.R
import com.example.feature.home.HomeScreen
import com.example.feature.onboarding.AgreementScreen
import com.example.feature.onboarding.LanguageScreen
import com.example.feature.onboarding.LoginScreen
import kotlinx.coroutines.delay

// 바텀바 항목 정의
data class NavItem(
    val route: String,
    val iconResId: Int,
    val iconClickResId: Int
)

val bottomBarItems = listOf(
    NavItem("상담 센터", R.drawable.ic_consultation_center, R.drawable.ic_consultation_center_click),
    NavItem("채팅 상담", R.drawable.ic_chat_consultation2, R.drawable.ic_chat_consultation2_click),
    NavItem("홈", R.drawable.ic_home, R.drawable.ic_home_click),
    NavItem("이력서 작성", R.drawable.ic_resume_writing, R.drawable.ic_resume_writing_click),
    NavItem("커뮤니티", R.drawable.ic_community, R.drawable.ic_community_click)
)

// 바텀 네비게이션 UI
@Composable
fun MyBottomNavigation(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        bottomBarItems.forEach { item ->
            val isSelected = currentRoute == item.route
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
                        tint = Color.Unspecified
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
        composable("이용 동의") { AgreementScreen() }
        composable("홈") { HomeScreen() }
        composable("상담 센터") { Text("상담 센터 화면") }
        composable("채팅 상담") { Text("채팅 상담 화면") }
        composable("이력서 작성") { Text("이력서 작성 화면") }
        composable("커뮤니티") { Text("커뮤니티 화면") }
    }
}

// MainScreen 진입점
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 바텀바가 보일 화면만 정의
    val bottomBarRoutes = bottomBarItems.map { it.route }

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
