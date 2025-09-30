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

    // 들어오자마자 체크 실행
    LaunchedEffect(Unit) {
        viewModel.checkAutoLogin()
    }

    when (isAutoLoginSuccess) {
        true -> {
            // 자동 로그인 성공했을 경우 홈으로 이동
            LaunchedEffect("home-nav") {
                navController.navigate("홈") {
                    popUpTo("스플래시") { inclusive = true }
                }
            }
        }
        false -> {
            // 자동 로그인 실패했을 경우 온보딩으로 이동
            LaunchedEffect("onboarding-nav") {
                delay(1000)
                navController.navigate("온보딩") {
                    popUpTo("스플래시") { inclusive = true }
                }
            }
        }
        null -> {
            // 아직 체크 중인 경우 기존 UI 유지
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
