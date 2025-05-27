package com.example.feature.ui.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldMain0
import com.example.core.ui.theme.HelloWorldMain500
import com.example.core.ui.theme.HelloWorldMain600
import com.example.core.ui.theme.HelloWorldMain700
import com.example.feature.R

@Composable
fun WithdrawComplete(
    onNavigateToLogin: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF9F9FC),
                            HelloWorldMain500
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Image(
                painter = painterResource(com.example.core.ui.R.drawable.logo_helloworld),
                contentDescription = null,
                colorFilter = ColorFilter.tint(HelloWorldMain600),
                modifier = Modifier
                    .size(210.dp)
                    .padding(top = 40.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y =(-40).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "탈퇴가 완료되었습니다.",
                    style = AppTypography.heading01,
                    color = HelloWorldMain700,
                )
                Text(
                    text = "지금까지 함께해주셔서 감사합니다.\n언제든 다시 찾아오셔도 좋아요.\nHelloWorld는 늘 곁에 있을게요.",
                    style = AppTypography.body02,
                    color = HelloWorldMain600,
                    textAlign = TextAlign.Center,
                )
            }
            Image(
                painter = painterResource(R.drawable.ic_mascot_seeya),
                contentDescription = null
            )
        }
        Text(
            text = "확인",
            style = AppTypography.heading01,
            color = HelloWorldMain0,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(HelloWorldMain500)
                .clickable { onNavigateToLogin() }
                .padding(top = 22.dp, bottom = 34.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WithdrawCompletePreview() {
    WithdrawComplete(
        onNavigateToLogin = {},
    )
}