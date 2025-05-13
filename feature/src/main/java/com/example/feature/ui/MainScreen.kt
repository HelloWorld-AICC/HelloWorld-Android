package com.example.feature.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.feature.R
import com.example.feature.ui.community.navigation.communityPostDetailScreen
import com.example.feature.ui.community.navigation.communityPostWriteScreen
import com.example.feature.ui.community.navigation.communityScreen
import com.example.feature.ui.community.navigation.navigateToCommunityPostDetail
import com.example.feature.ui.community.navigation.navigateToCommunityPostWrite

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

@Composable
fun MyNavigationHost(navController: NavHostController) {
    NavHost(navController, startDestination = "홈") {
        composable("상담 센터") { Text("상담 센터", modifier = Modifier.padding(16.dp)) }
        composable("채팅 상담") { Text("채팅 상담", modifier = Modifier.padding(16.dp)) }
        composable("홈") { Text("HelloWorld", modifier = Modifier.padding(16.dp)) }
        composable("이력서 작성") { Text("이력서 작성", modifier = Modifier.padding(16.dp)) }
        communityScreen(
            onWriteClick = navController::navigateToCommunityPostWrite,
            onPostClick = navController::navigateToCommunityPostDetail,
        )
        communityPostDetailScreen()
        communityPostWriteScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { MyBottomNavigation(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            MyNavigationHost(navController)
        }
    }
}
