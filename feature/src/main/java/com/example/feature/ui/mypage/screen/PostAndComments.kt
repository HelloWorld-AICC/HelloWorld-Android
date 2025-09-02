package com.example.feature.ui.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.core.ui.component.HWDropdownMenuBox
import com.example.core.ui.component.IconPosition
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain500
import com.example.core.util.extension.toCategoryName
import com.example.core.util.extension.toFormattedDate
import com.example.core.util.extension.truncateWithEllipsis
import com.example.feature.R
import com.example.feature.ui.mypage.viewmodel.MyPostAndCommentUiState
import com.example.feature.ui.mypage.viewmodel.PostAndCommentsViewModel
import com.example.model.mypage.Comment
import com.example.model.mypage.Community

@Composable
internal fun PostAndComments(
    onNavigateBack: () -> Unit,
    onNavigateCommunity: (Int, Int) -> Unit,
    viewModel: PostAndCommentsViewModel = hiltViewModel()
) {
    val selectedMenu by viewModel.selectedMenu.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    PostAndComments(
        onNavigateBack = onNavigateBack,
        onNavigateCommunity = onNavigateCommunity,
        selectedMenu = selectedMenu,
        uiState = uiState,
        changeMenu = viewModel::changeMenu,
        isLoading = isLoading,
        onLoadMore = viewModel::loadData
    )

}

@Composable
private fun PostAndComments(
    onNavigateBack: () -> Unit,
    onNavigateCommunity: (Int, Int) -> Unit,
    selectedMenu: String = "게시글",
    uiState: MyPostAndCommentUiState = MyPostAndCommentUiState.Loading,
    changeMenu: (String) -> Unit,
    isLoading: Boolean = false,
    onLoadMore: (String) -> Unit,
) {
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
                    padding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    selectedItem = selectedMenu,
                    items = options,
                    iconPosition = IconPosition.LEFT,
                    expanded = expanded,
                    iconSize = 12.dp,
                    onExpandedChange = { expanded = !expanded },
                    onClick = {
                        changeMenu(it)
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
            when (uiState) {
                is MyPostAndCommentUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is MyPostAndCommentUiState.Success<*> -> {
                    ListItems(
                        items = uiState.result,
                        isLoading = isLoading,
                        selectedMenu = selectedMenu,
                        onLoadMore = { onLoadMore(it) },
                        onItemClick = { categoryId, communityId ->
                            onNavigateCommunity(categoryId, communityId)
                        },
                    )
                }

                is MyPostAndCommentUiState.Error -> { }
            }
        }
    }
}

@Composable
private fun <T> ListItems (
    items: List<T> = emptyList(),
    isLoading: Boolean,
    selectedMenu: String,
    onLoadMore: (String) -> Unit,
    onItemClick: (Int, Int) -> Unit,
) {
    val density = LocalDensity.current

    if (items.isNotEmpty()) {
        val listState = rememberLazyListState()

        val shouldLoadMore by remember {
            derivedStateOf {
                val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItemsCount = listState.layoutInfo.totalItemsCount

                lastVisibleItemIndex >= totalItemsCount -1 && !isLoading
            }
        }

        LaunchedEffect(shouldLoadMore) {
            if (shouldLoadMore) { onLoadMore(selectedMenu) }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(
                    top = (with(density) {
                        48.dp + AppTypography.label01.fontSize.toDp()
                    })
                )
        ) {
            items(items) { item ->
                when (item) {
                    is Community -> {
                        CommunityItem(
                            item = item,
                            onItemClick = { categoryId, communityId ->
                                onItemClick(categoryId, communityId)
                            }
                        )
                    }

                    is Comment -> {
                        CommentItem(
                            item = item,
                            onItemClick = { categoryId, communityId ->
                                onItemClick(categoryId, communityId)
                            }
                        )
                    }
                }
            }
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = HelloWorldMain500
                        )
                    }
                }
            }
        }
    } else {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = HelloWorldMain500
                )
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

@Composable
private fun CommunityItem(
    item: Community,
    modifier: Modifier = Modifier,
    onItemClick: (categoryId: Int, communityId: Int) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(item.category.toInt(), item.communityId.toInt()) }
            .padding(vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = item.title.truncateWithEllipsis(20),
                    style = AppTypography.body02,
                    color = HelloWorldGrayScale800,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.content.truncateWithEllipsis(if (item.imageUrl != null) 30 else 40),
                    style = AppTypography.label01,
                    color = HelloWorldGrayScale500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (item.imageUrl != null) {
                Spacer(modifier = Modifier.width(40.dp))
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 카테고리, 날짜
            Text(
                text = "${item.category.toCategoryName()} • ${item.uploadedAt.toFormattedDate()}",
                style = AppTypography.label03,
                color = HelloWorldGrayScale500
            )
            // 댓글
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_comment),
                    contentDescription = null,
                    modifier = Modifier
                        .size(10.dp),
                    tint = HelloWorldGrayScale500
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${item.commentCnt}",
                    style = AppTypography.label03,
                    color = HelloWorldGrayScale500
                )
            }
        }
    }
}

@Composable
private fun CommentItem(
    item: Comment,
    modifier: Modifier = Modifier,
    onItemClick: (Int, Int) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(item.categoryId.toInt(), item.communityId.toInt()) }
            .padding(vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = item.communityTitle.truncateWithEllipsis(20),
                    style = AppTypography.body02,
                    color = HelloWorldGrayScale800,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.commentContent.truncateWithEllipsis(40),
                    style = AppTypography.label01,
                    color = HelloWorldGrayScale500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 카테고리, 날짜
            Text(
                text = "$${item.categoryId.toCategoryName()} • ${item.uploadedAt.toFormattedDate()}",
                style = AppTypography.label03,
                color = HelloWorldGrayScale500
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PostAndCommentsPreview() {
    PostAndComments(
        onNavigateBack = {},
        onNavigateCommunity = {_, _ -> }
    )
}