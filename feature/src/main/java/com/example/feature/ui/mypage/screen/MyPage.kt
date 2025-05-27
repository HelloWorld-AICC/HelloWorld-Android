package com.example.feature.ui.mypage.screen

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.component.DialogData
import com.example.core.ui.component.HWDialog
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain500
import com.example.feature.BuildConfig
import com.example.feature.R
import com.example.feature.ui.mypage.viewmodel.MyPageViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

@Composable
fun MyPage(
    onNavigateBack: () -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToCounselingSummary: () -> Unit,
    onNavigateToResume: () -> Unit,
    onNavigateToPostAndComments: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToTermsOfService: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
    viewModel: MyPageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val dialogData by viewModel.dialogData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(start = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_keyboard_arrow_left),
                contentDescription = null,
                tint = HelloWorldMain500,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onNavigateBack() }
                    .padding(8.dp)
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "마이페이지",
                style = AppTypography.heading04,
                color = HelloWorldGrayScale800
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 38.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Image(
                    painter = painterResource(R.drawable.ic_mascot_normal_profile),
                    contentDescription = null
                )
                Icon(
                    Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "NaYeEun",
                    style = AppTypography.heading01,
                    color = HelloWorldGrayScale800,
                )
                Text(
                    text = "오늘도 낯선 땅에서 열심히 살아가는 당신을, 헬로월드가 항상 응원하고 있어요",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W500,
                    color = HelloWorldGrayScale300,
                    lineHeight = 12.sp
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            MenuCard(
                title = "프로필 변경",
                onClick = onNavigateToProfileEdit,
            )
            MenuCard(
                title = "내 상담 요약",
                onClick = onNavigateToCounselingSummary,
            )
            MenuCard(
                title = "이력서\n/ 자기소개서",
                spacing = 6.dp,
                onClick = onNavigateToResume,
            )
            MenuCard(
                title = "게시글 / 댓글",
                onClick = onNavigateToPostAndComments,
            )
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Text(
                    text = "Hello World 정보",
                    style = AppTypography.body01,
                    color = HelloWorldMain500,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToTermsOfService() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "서비스 이용약관",
                        style = AppTypography.body02,
                        color = HelloWorldGrayScale800,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_keyboard_arrow_right),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPrivacyPolicy() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "개인정보 처리방침",
                        style = AppTypography.body02,
                        color = HelloWorldGrayScale800,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_keyboard_arrow_right),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            OssLicensesMenuActivity.setActivityTitle("오픈소스 라이선스")
                            context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "오픈소스 라이선스",
                        style = AppTypography.body02,
                        color = HelloWorldGrayScale800,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_keyboard_arrow_right),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "앱 버전",
                        style = AppTypography.body02,
                        color = HelloWorldGrayScale800,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = BuildConfig.APP_VERSION_NAME,
                        style = AppTypography.body02,
                        color = HelloWorldMain500,
                        modifier = Modifier
                            .padding(end = 8.dp)
                    )
                }
            }
            Column {
                Text(
                    text = "계정",
                    style = AppTypography.body01,
                    color = HelloWorldMain500,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.updateDialogData(
                                DialogData(
                                    title = "로그아웃 하시겠어요?",
                                    subTitle = "다음에 다시 만나요!",
                                    dismiss = "취소",
                                    confirm = "로그아웃",
                                    onDismiss = { viewModel.updateDialogData() },
                                    onConfirm = {
                                        // TODO 로그아웃 처리
                                        viewModel.updateDialogData()
                                    }
                                )
                            )
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "로그아웃",
                        style = AppTypography.body02,
                        color = HelloWorldGrayScale800,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_keyboard_arrow_right),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToWithdraw() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "탈퇴하기",
                        style = AppTypography.body02,
                        color = HelloWorldGrayScale800,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_keyboard_arrow_right),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                }
            }
        }
    }

    dialogData?.let {
        HWDialog(it)
    }
}

@Composable
private fun MenuCard(
    title: String = "",
    spacing: Dp = 12.dp,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(spacing))
        Text(
            text = title,
            style = AppTypography.label03,
            color = HelloWorldGrayScale800,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPagePreview() {
    MyPage(
        onNavigateBack = {},
        onNavigateToProfileEdit = {  },
        onNavigateToCounselingSummary = {},
        onNavigateToResume = {},
        onNavigateToPostAndComments = {},
        onNavigateToPrivacyPolicy = {},
        onNavigateToTermsOfService = {},
        onNavigateToWithdraw = {},
    )
}