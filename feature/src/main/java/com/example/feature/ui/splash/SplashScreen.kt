package com.example.feature.ui.splash

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.navigation.NavController
import com.example.core.ui.theme.HelloWorldMain0
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // 1초 후 OnboardingScreen으로 이동
    LaunchedEffect(Unit) {
        delay(1000)
        navController.navigate("온보딩") {
            popUpTo("스플래시") { inclusive = true }
        }
    }

    // UI 구성
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HelloWorldMain0)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            SplashLogo()
            SplashImg()
        }
    }
}
