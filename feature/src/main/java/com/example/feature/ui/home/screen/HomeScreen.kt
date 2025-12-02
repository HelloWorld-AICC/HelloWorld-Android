package com.example.feature.ui.home.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
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
import com.example.feature.ui.mypage.navigation.navigateToMyPage
import com.example.feature.ui.home.viewmodel.HomeViewModel
import com.example.core.data.network.RetrofitInstance

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
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
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
                        text = "님",
                        style = AppTypography.heading01,
                        color = HelloWorldGrayScale500
                    )
                }
                Text(
                    text = "안녕하세요!",
                    style = AppTypography.heading01,
                    color = HelloWorldGrayScale500
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "마이페이지 가기 →",
                    style = AppTypography.label02,
                    color = HelloWorldMain600,
                    modifier = Modifier.clickable {
                        Log.d("홈화면", "마이페이지로 이동 클릭")
                        navController.navigateToMyPage()
                    }
                )
            }

            Image(
                painter = painterResource(id = R.drawable.avatar_character),
                contentDescription = "사용자 아바타",
                modifier = Modifier.size(140.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 서비스 카드 목록
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .padding(24.dp, 30.dp, 24.dp, 19.dp)
        ) {
            Text(
                text = "헬로월드 서비스",
                style = AppTypography.label01,
                color = HelloWorldGrayScale500
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HomeServiceCard(
                    title = "챗봇 상담",
                    subtitle = "24시간 고민 상담",
                    iconRes = R.drawable.ic_service_chat,
                    backgroundColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
                HomeServiceCard(
                    title = "AI 자기소개서",
                    subtitle = "AI와 함께 쉽게 작성해요",
                    iconRes = R.drawable.ic_service_ai,
                    backgroundColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HomeServiceCard(
                    title = "내 주변 상담센터",
                    subtitle = "상담이 필요할 때, 바로 여기",
                    iconRes = R.drawable.ic_service_location,
                    backgroundColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
                HomeServiceCard(
                    title = "커뮤니티",
                    subtitle = "함께 이야기하고 공감해요",
                    iconRes = R.drawable.ic_service_community,
                    backgroundColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }

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
    modifier: Modifier
) {
    val shadowColor = Color(0x665E8DC5)
    Box(
        modifier = modifier
            .aspectRatio(1.3f)
            .graphicsLayer {
                shadowElevation = 4.dp.toPx()
                shape = RoundedCornerShape(8.dp)
            }
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .shadow(elevation = 1.dp, ambientColor = shadowColor, spotColor = shadowColor)
            .padding(12.dp, 16.dp, 12.dp, 8.dp),
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
