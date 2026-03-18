package com.example.feature.ui.splash.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.core.ui.theme.HelloWorldMain0
import com.example.feature.ui.splash.SplashImg
import com.example.feature.ui.splash.SplashLogo
import com.example.feature.ui.splash.SplashViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isAutoLoginSuccess by viewModel.isAutoLoginSuccess.collectAsState()
    var isMinimumSplashTimeElapsed by remember { mutableStateOf(false) }

    // 들어오자마자 체크 실행
    LaunchedEffect(Unit) {
        viewModel.checkAutoLogin()
    }

    LaunchedEffect(Unit) {
        delay(5000)
        isMinimumSplashTimeElapsed = true
    }

    when {
        isAutoLoginSuccess == true && isMinimumSplashTimeElapsed -> {
            LaunchedEffect("home-nav") {
                navController.navigate("홈") {
                    popUpTo("스플래시") { inclusive = true }
                }
            }
        }
        isAutoLoginSuccess == false && isMinimumSplashTimeElapsed -> {
            LaunchedEffect("onboarding-nav") {
                navController.navigate("온보딩") {
                    popUpTo("스플래시") { inclusive = true }
                }
            }
        }
        else -> {
            SplashUI()
        }
    }
}

@Composable
private fun SplashUI() {
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
