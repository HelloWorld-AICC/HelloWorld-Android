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
import com.example.feature.home.HomeScreen

data class NavItem(
    val route: String,
    val iconResId: Int,
    val iconClickResId: Int
)

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

@Composable
fun MyNavigationHost(navController: NavHostController) {
    NavHost(navController, startDestination = "홈") {
        composable("상담 센터") { Text("상담 센터", modifier = Modifier.padding(16.dp)) }
        composable("채팅 상담") { Text("채팅 상담", modifier = Modifier.padding(16.dp)) }
        composable("홈") { HomeScreen() }
        composable("이력서 작성") { Text("이력서 작성", modifier = Modifier.padding(16.dp)) }
        composable("커뮤니티") { Text("커뮤니티", modifier = Modifier.padding(16.dp)) }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { MyBottomNavigation(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            MyNavigationHost(navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
