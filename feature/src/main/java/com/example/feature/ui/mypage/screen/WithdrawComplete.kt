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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.ui.R
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldMain0
import com.example.core.ui.theme.HelloWorldMain500
import com.example.core.ui.theme.HelloWorldMain600
import com.example.core.ui.theme.HelloWorldMain700

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
                painter = painterResource(R.drawable.logo_helloworld),
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
                    text = stringResource(R.string.mypage_withdraw_complete_title),
                    style = AppTypography.heading01,
                    color = HelloWorldMain700,
                )
                Text(
                    text = stringResource(R.string.mypage_withdraw_complete_message),
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
            text = stringResource(R.string.confirm),
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