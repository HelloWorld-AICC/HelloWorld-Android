package com.example.feature.ui.mypage.screen

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain200
import com.example.core.ui.theme.HelloWorldMain500
import com.example.feature.R
import com.example.feature.ui.mypage.viewmodel.CounselingSummaryUiState
import com.example.feature.ui.mypage.viewmodel.CounselingSummaryViewModel

@Composable
fun CounselingSummary(
    onNavigateBack: () -> Unit,
    onNavigateToCounselingDetail: () -> Unit,
    viewModel: CounselingSummaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CounselingSummary(
        onNavigateBack = onNavigateBack,
        onNavigateToCounselingDetail = onNavigateToCounselingDetail,
        uiState = uiState,
        getAllSummary = viewModel::getAllSummary
    )
}

@Composable
private fun CounselingSummary(
    onNavigateBack: () -> Unit,
    onNavigateToCounselingDetail: () -> Unit,
    uiState: CounselingSummaryUiState,
    getAllSummary: (page: Int, size: Int) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
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
                text = "내 상담 요약",
                style = AppTypography.heading04,
                color = HelloWorldGrayScale800
            )
        }
        when (uiState) {
            is CounselingSummaryUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is CounselingSummaryUiState.Success -> {
                if (uiState.result.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_mascot_error),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "아직 상담내역이 없어요.\n링고와 함께 AI상담을 시작해보세요.",
                            style = AppTypography.label02,
                            color = HelloWorldGrayScale300,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                    ) {
                        items(uiState.result) {
                            ChatItem(
                                onClick = { onNavigateToCounselingDetail() }
                            )
                        }
                    }
                }
            }
            is CounselingSummaryUiState.Error -> {}
        }
    }
}

@Composable
fun ChatItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_chat_item),
                        contentDescription = null
                    )
                    Text(
                        text = "chat 요약",
                        style = AppTypography.label01,
                        color = HelloWorldGrayScale300
                    )
                }
                Text(
                    text = "임금 체불과 직장 내 괴롭힘",
                    style = AppTypography.heading04,
                    color = HelloWorldGrayScale800,
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_keyboard_arrow_right),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(),
            thickness = 1.dp,
            color = HelloWorldMain200
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CounselingSummaryPreview() {
    CounselingSummary(
        onNavigateBack = {},
        onNavigateToCounselingDetail = {}
    )
}