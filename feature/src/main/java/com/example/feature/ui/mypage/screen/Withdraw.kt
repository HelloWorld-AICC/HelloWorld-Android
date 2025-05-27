package com.example.feature.ui.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.component.DialogData
import com.example.core.ui.component.HWDialog
import com.example.core.ui.component.HWDropdownMenuBox
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale100
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain0
import com.example.core.ui.theme.HelloWorldMain500
import com.example.feature.R
import com.example.feature.ui.mypage.viewmodel.WithdrawViewModel

@Composable
fun Withdraw(
    onNavigateBack: () -> Unit,
    onNavigateToWithdrawComplete: () -> Unit,
    viewModel: WithdrawViewModel = hiltViewModel()
) {
    val dialogData by viewModel.dialogData.collectAsState()

    // TODO viewmodel
    val options: List<String> = listOf("필요한 기능이 없어요", "도움이 되지 않아요", "이용이 어려워요", "더 이상 한국에서 근로생활은 하지 않아요")
    var selected by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

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
                text = "탈퇴하기",
                style = AppTypography.heading04,
                color = HelloWorldGrayScale800
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_mascot_withdraw),
                contentDescription = null,
                modifier = Modifier
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "정말 탈퇴하실 건가요?",
                    style = AppTypography.title03,
                    color = HelloWorldGrayScale800,
                )
                Text(
                    text = "탈퇴하신 후에도 작성하신 글과 댓글은 서비스에 남을 수 있으며,\n이후에는 수정·삭제 등 관리가 불가능합니다.",
                    style = AppTypography.label02,
                    color = HelloWorldGrayScale500,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(44.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "탈퇴를 결심하게 된 이유를 알려주세요",
                style = AppTypography.label01,
                color = HelloWorldGrayScale800,
            )
            HWDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth(),
                selectedItem = selected,
                items = options,
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                onClick = {
                    selected = it
                    expanded = false
                },
                selectedContent = { item ->
                    Text(
                        text = item ?: "이유를 선택해주세요",
                        style = AppTypography.heading04,
                        color = if (item == null) HelloWorldGrayScale300 else HelloWorldGrayScale800
                    )
                },
                itemContent = { item, isSelected ->
                    Text(
                        text = item,
                        style = AppTypography.heading04,
                        color = HelloWorldGrayScale800
                    )
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "탈퇴하기",
                style = AppTypography.heading01,
                color = HelloWorldGrayScale500,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(HelloWorldGrayScale100)
                    .weight(1f)
                    .clickable(
                        enabled = selected != null
                    ) {
                        viewModel.updateDialogData(
                            DialogData(
                                title = "탈퇴 하시겠어요?",
                                subTitle = "게시글과 댓글에 대한 권한이 없어집니다",
                                dismiss = "취소",
                                confirm = "탈퇴하기",
                                onDismiss = { viewModel.updateDialogData() },
                                onConfirm = {
                                    // TODO api 추가
                                    viewModel.updateDialogData()
                                    onNavigateToWithdrawComplete()
                                }
                            )
                        )
                    }
                    .padding(vertical = 22.dp)
            )
            Text(
                text = "취소",
                style = AppTypography.heading01,
                color = HelloWorldMain0,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(HelloWorldMain500)
                    .weight(1f)
                    .clickable { onNavigateBack() }
                    .padding(vertical = 22.dp)
            )
        }
    }

    dialogData?.let {
        HWDialog(it)
    }
}

@Preview(showBackground = true)
@Composable
fun WithdrawPreview() {
    Withdraw(
        onNavigateBack = {},
        onNavigateToWithdrawComplete = {},
    )
}