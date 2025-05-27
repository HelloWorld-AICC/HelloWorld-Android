package com.example.feature.ui.mypage.screen

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain500
import com.example.feature.R
import com.example.feature.ui.mypage.viewmodel.CounselingDetailViewModel

@Composable
fun CounselingDetail(
    onNavigateBack: () -> Unit,
    onNavigateToAIChatDetail: (Int) -> Unit,
    viewModel: CounselingDetailViewModel = hiltViewModel()
) {
    val summaryText by viewModel.summaryText.collectAsState()

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
        ChatItem(
            modifier = Modifier
                .padding(horizontal = 24.dp),
            onClick = { onNavigateToAIChatDetail(1) }
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = summaryText,
            style = AppTypography.body02,
            color = HelloWorldGrayScale500,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CounselingDetailPreview() {
    CounselingDetail(
        onNavigateBack = {},
        onNavigateToAIChatDetail = {}
    )
}