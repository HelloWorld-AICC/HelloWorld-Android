package com.example.feature.ui.onboarding.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.example.core.ui.components.BottomButton
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.feature.R
import com.example.core.ui.R as languageR

@Composable
fun CongratulationsScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            BottomButton(
                text = stringResource(languageR.string.signup_complete_go_home),
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
                text = stringResource(languageR.string.signup_complete_go_home),
                style = AppTypography.heading01,
                color = HelloWorldGrayScale800
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(languageR.string.auth_tagline),
                style = AppTypography.body02,
                color = HelloWorldGrayScale300
            )
        }
    }
}
