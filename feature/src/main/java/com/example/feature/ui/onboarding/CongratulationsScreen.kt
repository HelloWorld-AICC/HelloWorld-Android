package com.example.feature.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.example.core.ui.components.BottomButton
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.feature.R

@Composable
fun CongratulationsScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            BottomButton(
                text = "홈으로 가기",
                onClick = { navController.navigate("홈") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Box로 겹치기
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Lottie 애니메이션
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.Asset("Congratulations.json")
                )
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )

                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                )

                // 캐릭터 이미지
                Image(
                    painter = painterResource(id = R.drawable.congratulation_character),
                    contentDescription = "language character",
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "회원가입이 완료되었습니다!",
                style = AppTypography.heading01,
                color = HelloWorldGrayScale800
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "삶과 일이 이어지는 그 모든 순간을 함께",
                style = AppTypography.body02,
                color = HelloWorldGrayScale300
            )
        }
    }
}
