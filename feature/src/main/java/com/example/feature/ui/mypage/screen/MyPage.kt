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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.core.ui.component.DialogData
import com.example.core.ui.component.HWDialog
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain500
import com.example.feature.BuildConfig
import com.example.feature.ui.mypage.viewmodel.MyPageUiState
import com.example.feature.ui.mypage.viewmodel.MyPageViewModel
import com.example.model.common.Language
import com.example.model.mypage.UserInfo
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.example.core.ui.R

@Composable
fun MyPage(
    onNavigateBack: () -> Unit,
    onNavigateToProfileEdit: (String, String?, Language?) -> Unit,
    onNavigateToCounselingSummary: () -> Unit,
    onNavigateToResume: () -> Unit,
    onNavigateToPostAndComments: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
    onCheckProfileUpdate: () -> Boolean,
    onClearProfileUpdate: () -> Unit,
    viewModel: MyPageViewModel = hiltViewModel()
) {
    val dialogData by viewModel.dialogData.collectAsState()

    val uiState by viewModel.uiState.collectAsState()

    MyPage(
        onNavigateBack = onNavigateBack,
        onNavigateToProfileEdit = onNavigateToProfileEdit,
        onNavigateToCounselingSummary = onNavigateToCounselingSummary,
        onNavigateToResume = onNavigateToResume,
        onNavigateToPostAndComments = onNavigateToPostAndComments,
        onNavigateToLogin= onNavigateToLogin,
        onNavigateToWithdraw = onNavigateToWithdraw,
        onCheckProfileUpdate = onCheckProfileUpdate,
        onClearProfileUpdate = onClearProfileUpdate,
        onLogout = viewModel::logout,
        uiState = uiState,
        dialogData = dialogData,
        updateDialogData = viewModel::updateDialogData,
        onChangeUserInfo = viewModel::getUserInfo
    )

}

@Composable
private fun MyPage(
    onNavigateBack: () -> Unit = {},
    onNavigateToProfileEdit: (String, String?, Language?) -> Unit,
    onNavigateToCounselingSummary: () -> Unit = {},
    onNavigateToResume: () -> Unit = {},
    onNavigateToPostAndComments: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToWithdraw: () -> Unit = {},
    onCheckProfileUpdate: () -> Boolean,
    onClearProfileUpdate: () -> Unit,
    onLogout: ((Boolean) -> Unit) -> Unit,
    uiState: MyPageUiState = MyPageUiState.Loading,
    dialogData: DialogData? = null,
    updateDialogData: (DialogData?) -> Unit,
    onChangeUserInfo: () -> Unit = {},
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val isUpdate = onCheckProfileUpdate()
        if (isUpdate) {
            onChangeUserInfo()
            onClearProfileUpdate()
        }
    }

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
        when (uiState) {
            is MyPageUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MyPageUiState.Success -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                        .padding(horizontal = 38.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                    ) {
                        if (uiState.userInfo.userImg != null) {
                            AsyncImage(
                                model = uiState.userInfo.userImg,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.ic_mascot_normal_profile),
                                contentDescription = null
                            )
                        }
                        Text(
                            text = "${uiState.userInfo.language?.flag}",
                            fontSize = 28.sp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = uiState.userInfo.name,
                            style = AppTypography.heading01,
                            color = HelloWorldGrayScale800,
                        )
                        Text(
                            text = stringResource(R.string.mypage_welcome_message),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.W500,
                            color = HelloWorldGrayScale300,
                            lineHeight = 12.sp
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MenuCard(
                        title = stringResource(R.string.mypage_profile_edit),
                        onClick = { onNavigateToProfileEdit(uiState.userInfo.name, uiState.userInfo.userImg, uiState.userInfo.language) }, // TODO language
                    )
                    MenuCard(
                        title = stringResource(R.string.mypage_consultation_summary),
                        onClick = { onNavigateToCounselingSummary() },
                    )
                    MenuCard(
                        title = stringResource(R.string.mypage_resume),
                        spacing = 6.dp,
                        onClick = { onNavigateToResume() },
                    )
                    MenuCard(
                        title = stringResource(R.string.mypage_posts_comments),
                        onClick = { onNavigateToPostAndComments() },
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.mypage_app_info),
                            style = AppTypography.body01,
                            color = HelloWorldMain500,
                            modifier = Modifier
                                .height(35.dp)
                                .padding(vertical = 8.dp),
                        )
                        MenuListItem(
                            title = stringResource(R.string.terms_service),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, "https://burnt-cellar-02c.notion.site/HelloWorld-262d4f1c212180c0bdd6cddbffacbc31?source=copy_link".toUri())
                                context.startActivity(intent)
                            }
                        )
                        MenuListItem(
                            title = stringResource(R.string.terms_privacy),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, "https://burnt-cellar-02c.notion.site/HelloWorld-262d4f1c212180049db4c7972cf4c6f2?source=copy_link".toUri())
                                context.startActivity(intent)
                            }
                        )
                        MenuListItem(
                            title = stringResource(R.string.mypage_opensource_license),
                            onClick = {
                                OssLicensesMenuActivity.setActivityTitle("오픈소스 라이선스")
                                context.startActivity(
                                    Intent(
                                        context,
                                        OssLicensesMenuActivity::class.java
                                    )
                                )
                            }
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.mypage_app_version),
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
                            text = stringResource(R.string.mypage_account),
                            style = AppTypography.body01,
                            color = HelloWorldMain500,
                            modifier = Modifier
                                .height(35.dp)
                                .padding(vertical = 8.dp)
                        )
                        MenuListItem(
                            title = stringResource(R.string.logout),
                            onClick = {
                                updateDialogData(
                                    DialogData(
                                        title = R.string.mypage_logout_dialog_title,
                                        subTitle = R.string.mypage_logout_dialog_subtitle,
                                        dismiss = R.string.community_post_cancel,
                                        confirm = R.string.logout,
                                        onDismiss = { updateDialogData(null) },
                                        onConfirm = {
                                            updateDialogData(null)
                                            onLogout { result ->
                                                if (result) {
                                                    onNavigateToLogin()
                                                }
                                            }
                                        }
                                    )
                                )
                            }
                        )
                        MenuListItem(
                            title = stringResource(R.string.withdraw),
                            onClick = { onNavigateToWithdraw() }
                        )
                    }
                }
            }
            is MyPageUiState.Error -> {} // TODO
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
            textAlign = TextAlign.Center,
            modifier = Modifier
                .widthIn(max = 60.dp)
        )
    }
}

@Composable
private fun MenuListItem(
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = AppTypography.body02,
            color = HelloWorldGrayScale800,
        )
        Icon(
            painter = painterResource(R.drawable.ic_keyboard_arrow_right),
            contentDescription = null,
            tint = Color.Unspecified,
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun MyPagePreview() {
    MyPage(
        onNavigateBack = {},
        onNavigateToProfileEdit = { _, _, _ -> },
        onNavigateToCounselingSummary = {},
        onNavigateToResume = {},
        onNavigateToPostAndComments = {},
        onNavigateToWithdraw = {},
        uiState = MyPageUiState.Success(
            UserInfo(
                name = "JParkBro",
                userImg = "",
                language = Language.KOREAN
            )
        ),
        updateDialogData = {},
        onCheckProfileUpdate = { false },
        onClearProfileUpdate = {},
        onLogout = {},
    )
}