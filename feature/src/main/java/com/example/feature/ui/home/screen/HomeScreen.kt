package com.example.feature.ui.home.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.feature.ui.home.header.LogoHeader
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldMain200
import com.example.core.ui.theme.HelloWorldMain400
import com.example.core.ui.theme.HelloWorldMain600
import com.example.core.ui.theme.HelloWorldMain700
import com.example.feature.R
import com.example.core.ui.R as languageR
import com.example.feature.ui.mypage.navigation.navigateToMyPage
import com.example.feature.ui.home.viewmodel.HomeViewModel
import com.example.core.data.network.RetrofitInstance
import com.example.core.ui.theme.HelloWorldGoogleBorder
import com.example.core.ui.theme.HelloWorldMain100
import com.example.core.ui.theme.HelloWorldMain300
import com.example.feature.ui.aichat.navigation.navigateToAIChat
import com.example.feature.ui.aichat.navigation.navigateToAIChatDetail
import com.example.feature.ui.community.navigation.navigateToCommunity

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val token = RetrofitInstance.getAccessToken()

    // 토큰 상태 로그
    Log.d("홈화면", "저장된 액세스 토큰: $token")

    LaunchedEffect(token) {
        if (token.isNotBlank()) {
            Log.d("홈화면", "토큰이 존재하므로 사용자 정보 요청 시작")
            viewModel.fetchUserInfoIfTokenExists()
        } else {
            Log.w("홈화면", "토큰이 없습니다. 로그인 필요")
        }
    }

    val userInfo by viewModel.userInfo.collectAsState()

    Column(
        modifier = Modifier
            .background(color = HelloWorldMain200)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogoHeader()

        // 상단 사용자 정보 영역
        Row(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 34.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userInfo?.name ?: "이름 없음",
                        style = AppTypography.title02,
                        color = HelloWorldMain700
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource( languageR.string.home_greeting_suffix),
                        style = AppTypography.heading01,
                        color = HelloWorldGrayScale500
                    )
                }
                Text(
                    text = stringResource(languageR.string.home_greeting_message),
                    style = AppTypography.heading01,
                    color = HelloWorldGrayScale500
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.width(115.dp),
                    thickness = 1.dp,
                    color = HelloWorldMain300
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text =  stringResource(languageR.string.home_go_mypage) + " →",
                    style = AppTypography.label02,
                    color = HelloWorldMain600,
                    modifier = Modifier.clickable {
                        Log.d("홈화면", "마이페이지로 이동 클릭")
                        navController.navigateToMyPage()
                    }
                )
            }

            Spacer(modifier = Modifier.width(37.dp))

            Image(
                painter = painterResource(id = R.drawable.avatar_character),
                contentDescription = "사용자 아바타",
                modifier = Modifier
                    .width(140.dp)
                    .height(164.dp)
            )
        }
        // 서비스 카드 목록
        Column(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .padding(24.dp, 30.dp, 24.dp, 19.dp)
        ) {
            Text(
                text = stringResource(languageR.string.home_service_title),
                style = AppTypography.label01,
                color = HelloWorldGrayScale500
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                HomeServiceCard(
                    title = stringResource(languageR.string.home_chatbot_title),
                    subtitle = stringResource(languageR.string.home_chatbot_subtitle),
                    iconRes = R.drawable.ic_service_chat,
                    backgroundColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigateToAIChat() }
                )
                HomeServiceCard(
                    title = stringResource(languageR.string.home_ai_resume_title),
                    subtitle = stringResource(languageR.string.home_ai_resume_subtitle),
                    iconRes = R.drawable.ic_service_ai,
                    backgroundColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("이력서 작성") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                HomeServiceCard(
                    title = stringResource(languageR.string.home_nearby_center_title),
                    subtitle = stringResource(languageR.string.home_nearby_center_subtitle),
                    iconRes = R.drawable.ic_service_location,
                    backgroundColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("상담 센터")}
                )
                HomeServiceCard(
                    title = stringResource(languageR.string.home_community_title),
                    subtitle = stringResource(languageR.string.home_community_subtitle),
                    iconRes = R.drawable.ic_service_community,
                    backgroundColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigateToCommunity() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = HelloWorldMain100
            )
            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.banner_helloworld),
                contentDescription = "HelloWorld 배너",
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun HomeServiceCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    backgroundColor: Color,
    modifier: Modifier,
    onClick: () -> Unit,
) {

    val shadowColor = Color(0xFF0b458a)
    Box(
        modifier = modifier
            .shadow(elevation = 2.dp, ambientColor = shadowColor, spotColor = shadowColor, shape = RoundedCornerShape(8.dp))
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .clip(shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp, 16.dp, 12.dp, 8.dp)

    ) {
        Column {
            Column {
                Text(
                    text = title,
                    style = AppTypography.body01,
                    color = HelloWorldMain700
                )
                Text(
                    text = subtitle,
                    style = AppTypography.label03,
                    color = HelloWorldMain400
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "$title 아이콘",
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }
}
