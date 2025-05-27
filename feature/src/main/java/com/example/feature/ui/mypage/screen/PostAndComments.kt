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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.component.HWDropdownMenuBox
import com.example.core.ui.component.IconPosition
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain500
import com.example.feature.R
import com.example.feature.ui.community.CommunityPostItem
import com.example.feature.ui.mypage.viewmodel.PostAndCommentsViewModel

@Composable
fun PostAndComments(
    onNavigateBack: () -> Unit,
    viewModel: PostAndCommentsViewModel = hiltViewModel()
) {
    val selectedMenu by viewModel.selectedMenu.collectAsState()
    val posts by viewModel.posts.collectAsState()

    val density = LocalDensity.current

    // TODO viewmodel
    val options: List<String> = listOf("게시글", "댓글")
    var expanded by remember { mutableStateOf(false) }

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
                text = "게시글 / 댓글",
                style = AppTypography.heading04,
                color = HelloWorldGrayScale800
            )
        }
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .zIndex(10f),
                contentAlignment = Alignment.CenterEnd
            ) {
                HWDropdownMenuBox(
                    selectedItem = selectedMenu,
                    items = options,
                    iconPosition = IconPosition.LEFT,
                    expanded = expanded,
                    iconSize = 12.dp,
                    onExpandedChange = { expanded = !expanded },
                    onClick = {
                        viewModel.changeMenu(it)
                        expanded = false
                    },
                    modifier = Modifier
                        .width(80.dp)
                        .zIndex(10f),
                    selectedContent = { item ->
                        Text(
                            text = item.toString(),
                            style = AppTypography.label01,
                            color = HelloWorldMain500,
                            modifier = Modifier
                                .padding(start = 4.dp)
                        )
                    },
                    itemContent = { item, isSelected ->
                        Text(
                            text = item,
                            style = AppTypography.label01,
                            color = HelloWorldGrayScale300,
                            textAlign = TextAlign.End,
                        )
                    },
                    hideSelectedItem = true
                )
            }
            if (posts.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .padding(top = (
                                with(density) {
                                    48.dp + AppTypography.label01.fontSize.toDp()
                                }
                        ))
                ) {
                    items(posts) { post ->
                        CommunityPostItem(
                            post = post,
                            onPostClick = {  }
                        )
                        // TODO 댓글 용 composable 만들기
                    }
                }
            } else {
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
                        text = "아직 게시물이 없어요.\n첫 번째 게시물을 작성해보세요!",
                        style = AppTypography.label02,
                        color = HelloWorldGrayScale300,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PostAndCommentsPreview() {
    PostAndComments(
        onNavigateBack = {}
    )
}