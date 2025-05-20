package com.example.feature.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale100
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain200
import com.example.core.ui.theme.HelloWorldMain400
import com.example.core.ui.theme.HelloWorldMain500
import com.example.core.util.extension.advancedImePadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CommunityPostWrite(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostWriteViewModel = hiltViewModel()
) {
    val selectedTab = viewModel.selectedTab.collectAsState()
    val title = viewModel.title.collectAsState()
    val content = viewModel.content.collectAsState()

    val posts = remember { mutableStateListOf<String>() }
    val charRange = ('A'..'Z')

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                focusManager.clearFocus()
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(start = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = HelloWorldMain500,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onBackClick() }
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "게시글 작성",
                style = AppTypography.heading04,
                color = HelloWorldGrayScale800
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = HelloWorldMain200,
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 12.dp)
            ) {
                Text(
                    text = "카테고리",
                    style = AppTypography.label01,
                    color = HelloWorldGrayScale800,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TabIconAndLabel(
                        title = "직장 내 고충",
                        icon = Icons.Default.Build,
                        onIconClick = { viewModel.changeTab("problem") },
                        isSelected = selectedTab.value == "problem",
                    )
                    TabIconAndLabel(
                        title = "체류 및 비자",
                        icon = Icons.Default.Call,
                        onIconClick = { viewModel.changeTab("national") },
                        isSelected = selectedTab.value == "national",
                    )
                    TabIconAndLabel(
                        title = "산재 및 의료",
                        icon = Icons.Default.Favorite,
                        onIconClick = { viewModel.changeTab("medical") },
                        isSelected = selectedTab.value == "medical",
                    )
                    TabIconAndLabel(
                        title = "기타",
                        icon = Icons.Default.Info,
                        onIconClick = { viewModel.changeTab("etc") },
                        isSelected = selectedTab.value == "etc",
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "제목",
                    style = AppTypography.label01,
                    color = HelloWorldGrayScale800,
                )
                BasicTextField(
                    value = title.value,
                    onValueChange = { viewModel.updateTitle(it) },
                    singleLine = true,
                    textStyle = AppTypography.heading02.copy(color = HelloWorldGrayScale800),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                ) { innerTextField ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (title.value.isEmpty()) {
                            Text(
                                text = "제목을 작성해 주세요 ( 최대 50자 )",
                                style = AppTypography.heading02,
                                color = HelloWorldGrayScale300,
                            )
                        }
                        innerTextField()
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "내용",
                    style = AppTypography.label01,
                    color = HelloWorldGrayScale800,
                )
                BasicTextField(
                    value = content.value,
                    onValueChange = { viewModel.updateContent(it) },
                    textStyle = AppTypography.body02.copy(color = HelloWorldGrayScale800),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                ) { innerTextField ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(12.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        if (content.value.isEmpty()) {
                            Text(
                                text = "게시글을 작성해 주세요 ( 최대 2000자 )",
                                style = AppTypography.body02,
                                color = HelloWorldGrayScale300,
                            )
                        }
                        innerTextField()
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "사진",
                    style = AppTypography.label01,
                    color = HelloWorldGrayScale800,
                )
                FlowRow(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .heightIn(min = 74.dp)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (posts.size < 12) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(HelloWorldGrayScale100, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    val randomChar = charRange.random().toString()
                                    posts.add(randomChar)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                    }
                    // TODO Image 추가
                    posts.forEachIndexed { index, item ->
                        Box(
                            modifier = Modifier
                                .background(HelloWorldGrayScale100, RoundedCornerShape(8.dp))
                                .size(50.dp)
                                .clickable {
                                    posts.removeAt(index)
                                },
                            contentAlignment = Alignment.Center
                        ) { Text(text = item) }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "사진은 최대 10장까지 업로드 가능합니다",
                    style = AppTypography.label03,
                    color = HelloWorldGrayScale300,
                )
                Text(
                    text = "영상은 최대 2개까지 업로드 가능합니다",
                    style = AppTypography.label03,
                    color = HelloWorldGrayScale300,
                )
                Text(
                    text = "과도한 비방 및 욕설이 포함된 게시물은 신고에 의해 무통보 삭제될 수 있습니다",
                    style = AppTypography.label03,
                    color = HelloWorldGrayScale300,
                )
                Text(
                    text = "초상권•저작권 침해 등 위법 게시물은 관리자 판단으로 삭제될 수 있습니다",
                    style = AppTypography.label03,
                    color = HelloWorldGrayScale300,
                )
            }
        }
        TextButton(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .advancedImePadding(),
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFFF9FBFC),
                containerColor = HelloWorldMain400,
                disabledContentColor = HelloWorldGrayScale500,
                disabledContainerColor = HelloWorldGrayScale100,
            ),
            shape = RoundedCornerShape(0),
            contentPadding = PaddingValues(
                top = 22.dp,
                bottom = 36.dp,
            )
        ) {
            Text(
                text = "완료",
                style = AppTypography.heading01,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityPostWritePreview() {
    CommunityPostWrite(
        onBackClick = {}
    )
}