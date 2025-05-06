package com.example.feature.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material3.*
import androidx.navigation.compose.*
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.painter.Painter
import com.example.feature.R

val items = listOf(
    NavItem("chat_consultation", R.drawable.ic_chat_consultation),
    NavItem("resume_writing", R.drawable.ic_resume_writing),
    NavItem("home", R.drawable.ic_home),
    NavItem("community", R.drawable.ic_community),
    NavItem("consultation_center", R.drawable.ic_consultation_center),
)


data class NavItem(val route: String, val iconResId: Int)

@Preview
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


@Composable
fun MyNavigationHost(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        composable("chat_consultation") { Text("chat Consultation Screen", modifier = Modifier.padding(16.dp)) }
        composable("resume_writing") { Text("Resume Writing Screen", modifier = Modifier.padding(16.dp)) }
        composable("home") { Text("Home Screen", modifier = Modifier.padding(16.dp)) }
        composable("community") { Text("Community Screen", modifier = Modifier.padding(16.dp)) }
        composable("consultation_center") { Text("Consultation Center Screen", modifier = Modifier.padding(16.dp)) }
    }
}

@Composable
fun MyBottomNavigation(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary // ✅ Compose에서 지정한 색상
                    )
                },
                label = { Text(item.route.replaceFirstChar { it.uppercase() }) },
                selected = currentDestination?.route == item.route,
                onClick = {
                    if (currentDestination?.route != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }

}
