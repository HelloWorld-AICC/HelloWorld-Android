package com.example.feature.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.component.DialogData
import com.example.core.ui.component.HWDialog
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale100
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain200
import com.example.core.ui.theme.HelloWorldMain500
import com.example.core.util.extension.advancedImePadding
import com.example.feature.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CommunityPostDetail(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val commentText = viewModel.commentText.collectAsState()

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    val posts = remember { mutableStateListOf<String>() }
    val charRange = ('A'..'Z')
    LaunchedEffect(Unit) {
        for (i in 0..12) posts.add(charRange.random().toString())
    }

    // TODO viewmodel
    var expanded by remember { mutableStateOf(false) }
    val dialogData by viewModel.dialogData.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = interactionSource,
            ) {
                focusManager.clearFocus()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(start = 8.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
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
                Text(
                    text = "커뮤니티",
                    style = AppTypography.heading04,
                    color = HelloWorldGrayScale800
                )
            }
            Box() {
                Icon(
                    painter = painterResource(R.drawable.ic_more_vertical),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .clickable { expanded = true }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "수정하기") },
                        onClick = {}
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text(text = "삭제하기") },
                        onClick = {
                            viewModel.updateDialogData(
                                DialogData(
                                    title = "게시글을 삭제하시겠어요?",
                                    subTitle = "삭제된 게시글은 복구할 수 없습니다.",
                                    dismiss = "돌아가기",
                                    confirm = "신고하기",
                                    onDismiss = { viewModel.updateDialogData() },
                                    onConfirm = {
                                        // TODO api 추가
                                        viewModel.updateDialogData()
                                    },
                                )
                            )
                        }
                    )
                    // TODO 분기처리
                    DropdownMenuItem(
                        text = { Text(text = "신고하기") },
                        onClick = {
                            viewModel.updateDialogData(
                                DialogData(
                                    title = "게시글을 신고하시겠어요?",
                                    subTitle = "허위 신고 시 제재를 받을 수 있습니다.",
                                    dismiss = "돌아가기",
                                    confirm = "신고하기",
                                    onDismiss = { viewModel.updateDialogData() },
                                    onConfirm = {
                                        // TODO api 추가
                                        viewModel.updateDialogData()
                                    },
                                )
                            )
                        }
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(),
            thickness = 1.dp,
            color = HelloWorldMain200
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "직장 내 고충 • 25.05.06",
                            style = AppTypography.label02,
                            color = HelloWorldGrayScale500,
                        )
                        Text(
                            text = "월급이 제대로 안 들어 온 것 같아요.",
                            style = AppTypography.heading02,
                            color = HelloWorldGrayScale800,
                        )
                    }
                    Text(
                        text = "안녕하세요.\n" +
                                "이번 달 월급이 들어왔는데, 평소보다 금액이 많이 적어서 걱정돼요." +
                                " 근무 시간은 그대로였고 결근도 없었는데 왜 이런지 모르겠어요… \uD83D\uDE22\n" +
                                "혹시 회사에서 공제되는 항목이 있을 수 있는 건가요?\n" +
                                "어디서 확인해야 할지, 어떻게 문의해야 할지도 잘 모르겠어요.\n" +
                                "비슷한 경험 있으신 분 계시면 도와주시면 정말 감사하겠습니다!",
                        style = AppTypography.body02,
                        color = HelloWorldGrayScale500,
                    )
                    LazyRow(
                        modifier = Modifier
                            .height(100.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(posts) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(HelloWorldGrayScale300),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = it
                                )
                            }
                        }
                    }
                }
            }
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    thickness = 1.dp,
                    color = HelloWorldMain200
                )
            }
            items(10) {
                CommentItem(
                    onDeleteClick = {
                        viewModel.updateDialogData(
                            DialogData(
                                title = "댓글을 삭제하시겠어요?",
                                subTitle = "삭제된 댓글은 복구할 수 없습니다.",
                                dismiss = "돌아가기",
                                confirm = "삭제하기",
                                onDismiss = { viewModel.updateDialogData() },
                                onConfirm = {
                                    // TODO api 추가
                                    viewModel.updateDialogData()
                                },
                            )
                        )
                    },
                    onReportClick = {
                        viewModel.updateDialogData(
                            DialogData(
                                title = "댓글을 신고하시겠어요?",
                                subTitle = "허위 신고 시 제재를 받을 수 있습니다.",
                                dismiss = "돌아가기",
                                confirm = "신고하기",
                                onDismiss = { viewModel.updateDialogData() },
                                onConfirm = {
                                    // TODO api 추가
                                    viewModel.updateDialogData()
                                },
                            )
                        )
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .advancedImePadding()
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 10.dp, bottom = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = commentText.value,
                onValueChange = { viewModel.updateComment(it) },
                textStyle = AppTypography.label02,
                maxLines = 6,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, HelloWorldGrayScale100, RoundedCornerShape(8.dp))
                    .fillMaxWidth(),
            ) { innerTextField ->
                Box (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (commentText.value.isBlank()) {
                        Text(
                            text = "댓글을 남겨보세요",
                            style = AppTypography.label02,
                            color = HelloWorldGrayScale300,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(end = 40.dp)
                    ) {
                        innerTextField()
                    }
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_up),
                        contentDescription = null,
                        tint = if (commentText.value.isBlank()) HelloWorldGrayScale100 else Color.Unspecified,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .clickable(
                                enabled = commentText.value.isNotBlank()
                            ) {  }
                            .padding(4.dp)
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
private fun CommentItem(
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
    onReportClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "익명2",
                    style = AppTypography.label03,
                    color = HelloWorldGrayScale800,
                )
                Text(
                    text = "•",
                    style = AppTypography.label03,
                    color = HelloWorldGrayScale300
                )
                Text(
                    text = "25.05.06",
                    style = AppTypography.label03,
                    color = HelloWorldGrayScale300
                )
            }
            Box {
                Icon(
                    painter = painterResource(R.drawable.ic_more_vertical),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { expanded = true }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "수정하기") },
                        onClick = {}
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text(text = "삭제하기") },
                        onClick = { onDeleteClick() }
                    )
                    // TODO 분기처리
                    DropdownMenuItem(
                        text = { Text(text = "신고하기") },
                        onClick = { onReportClick() }
                    )
                }
            }
        }
        Text(
            text = "무료로 상담해주는 기관도 많아요. 지역마다 외국인 근로자 지원세터도 있으니까 도움 받기 쉬우실 거에요!",
            style = AppTypography.label02,
            color = HelloWorldGrayScale500
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityPostDetailPreview() {
    CommunityPostDetail(
        onNavigateBack = {}
    )
}