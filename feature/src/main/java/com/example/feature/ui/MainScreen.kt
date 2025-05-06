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

data class NavItem(val route: String, val iconResId: Int)

val items = listOf(
    NavItem("채팅 상담", R.drawable.ic_chat_consultation2),
    NavItem("상담 센터", R.drawable.ic_consultation_center2),
    NavItem("홈", R.drawable.ic_home2),
    NavItem("이력서 작성", R.drawable.ic_resume_writing2),
    NavItem("커뮤니티", R.drawable.ic_community2)
)

@Composable
fun MyBottomNavigation(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val selectedColor = Color(0xFFCFE3FD)
    val unselectedColor = Color(0xFFE1E1E1)

    NavigationBar(
        containerColor = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.route == item.route

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
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.route,
                        tint = if (isSelected) selectedColor else unselectedColor
                    )
                }
            )
        }
    }
}


@Composable
fun MyNavigationHost(navController: NavHostController) {
    NavHost(navController, startDestination = "홈") {
        composable("채팅 상담") { Text("채팅 상담", modifier = Modifier.padding(16.dp)) }
        composable("상담 센터") { Text("상담 센터", modifier = Modifier.padding(16.dp)) }
        composable("홈") { Text("HelloWorld", modifier = Modifier.padding(16.dp)) }
        composable("이력서 작성") { Text("이력서 작성", modifier = Modifier.padding(16.dp)) }
        composable("커뮤니티") { Text("커뮤니티", modifier = Modifier.padding(16.dp)) }
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
