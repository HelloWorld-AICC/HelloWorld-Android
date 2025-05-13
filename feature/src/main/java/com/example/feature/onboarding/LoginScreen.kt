package com.example.feature.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.core.ui.theme.Main0
import com.example.core.ui.theme.Main500
import com.example.core.ui.theme.Pretendard
import com.example.feature.R
import com.example.feature.splash.SplashImg
import com.example.feature.splash.SplashLogo

@Composable
fun LoginScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Main0)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            // 상단 로고
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SplashLogo()
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "삶과 일이 이어지는 그 모든 순간을 함께",
                    fontSize = 16.sp,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.SemiBold,
                    color = Main500
                )
            }

            // 중단 Google 버튼
            GoogleSignInButton(navController = navController)

            // 하단 일러스트
            SplashImg()
        }
    }
}

@Composable
fun GoogleSignInButton(navController: NavController) {
    Button(
        onClick = {
            // 언어 설정 화면으로 이동
            navController.navigate("언어 설정")
        },
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD4D4D4)),
        modifier = Modifier.height(48.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google",
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Continue with Google",
            fontFamily = Pretendard,
            fontWeight = FontWeight.SemiBold,
            color = Color(0XFF1F1F1F),
            fontSize = 14.sp
        )
    }
}
