package com.example.feature.ui.community

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain200
import com.example.core.ui.theme.HelloWorldMain500
import com.example.core.util.extension.toCategoryName
import com.example.core.util.extension.toFormattedDate
import com.example.core.util.extension.truncateWithEllipsis
import com.example.feature.R
import com.example.model.common.ContentType
import com.example.model.community.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Community(
    onNavigateToCommunityPostWrite: (Int, Int, ContentType) -> Unit,
    onNavigateToCommunityPostDetail: (Int, Int) -> Unit,
    onCheckCommunityUpdate: () -> Boolean,
    onClearCommunityUpdate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()

    val problemPosts by viewModel.problemPosts.collectAsState()
    val nationalPosts by viewModel.nationalPosts.collectAsState()
    val medicalPosts by viewModel.medicalPosts.collectAsState()
    val etcPosts by viewModel.etcPosts.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = selectedTab,
        pageCount = { 4 }
    )

    LaunchedEffect(selectedTab) {
        if (pagerState.currentPage != selectedTab) {
            pagerState.scrollToPage(selectedTab)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != selectedTab) {
            viewModel.changeTab(pagerState.currentPage)
        }
    }

    LaunchedEffect(Unit) {
        val isUpdate = onCheckCommunityUpdate()
        if (isUpdate) {
            viewModel.loadMorePosts(selectedTab, 0)
            onClearCommunityUpdate()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "커뮤니티",
                    style = AppTypography.body01,
                    color = HelloWorldGrayScale800
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabIconAndLabel(
                    title = "직장 내 고충",
                    icon = painterResource(R.drawable.ic_problem),
                    onIconClick = { viewModel.changeTab(0) },
                    isSelected = pagerState.currentPage == 0,
                )
                TabIconAndLabel(
                    title = "체류 및 비자",
                    icon = painterResource(R.drawable.ic_national),
                    onIconClick = { viewModel.changeTab(1) },
                    isSelected = pagerState.currentPage == 1,
                )
                TabIconAndLabel(
                    title = "산재 및 의료",
                    icon = painterResource(R.drawable.ic_medical),
                    onIconClick = { viewModel.changeTab(2) },
                    isSelected = pagerState.currentPage == 2,
                )
                TabIconAndLabel(
                    title = "기타",
                    icon = painterResource(R.drawable.ic_etc),
                    onIconClick = { viewModel.changeTab(3) },
                    isSelected = pagerState.currentPage == 3,
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = HelloWorldMain200)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> CommunityTabContent(
                        posts = problemPosts,
                        onPostClick = { onNavigateToCommunityPostDetail(0, it) },
                        onLoadMore = { viewModel.loadMorePosts(0) },
                        isLoading = isLoading,
                    )
                    1 -> CommunityTabContent(
                        posts = nationalPosts,
                        onPostClick = { onNavigateToCommunityPostDetail(1, it) },
                        onLoadMore = { viewModel.loadMorePosts(1) },
                        isLoading = isLoading,
                    )
                    2 -> CommunityTabContent(
                        posts = medicalPosts,
                        onPostClick = { onNavigateToCommunityPostDetail(2, it) },
                        onLoadMore = { viewModel.loadMorePosts(2) },
                        isLoading = isLoading,
                    )
                    3 -> CommunityTabContent(
                        posts = etcPosts,
                        onPostClick = { onNavigateToCommunityPostDetail(3, it) },
                        onLoadMore = { viewModel.loadMorePosts(3) },
                        isLoading = isLoading,
                    )
                }
            }
        }
        Image(
            painter = painterResource(R.drawable.ic_write),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(60.dp)
                .clip(CircleShape)
                .clickable { onNavigateToCommunityPostWrite(selectedTab, 0, ContentType.CREATE) }
                .border(1.dp, Color(0x40000000), shape = CircleShape)
        )
    }
}

@Composable
private fun CommunityTabContent(
    posts: List<Post>,
    onPostClick: (Int) -> Unit,
    onLoadMore: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    if (posts.isNotEmpty()) {
        val listState = rememberLazyListState()

        val shouldLoadMore by remember {
            derivedStateOf {
                val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItemsCount = listState.layoutInfo.totalItemsCount

                lastVisibleItemIndex >= totalItemsCount - 2 && !isLoading
            }
        }

        LaunchedEffect(shouldLoadMore) {
            if (shouldLoadMore) { onLoadMore() }
        }

        LazyColumn(
            state = listState,
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            items(posts) { post ->
                CommunityPostItem(
                    post = post,
                    onPostClick = { onPostClick(post.postId) }
                )
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
fun CommunityPostItem(
    post: Post,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPostClick() }
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
                    text = post.title.truncateWithEllipsis(20),
                    style = AppTypography.body02,
                    color = HelloWorldGrayScale800,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.content.truncateWithEllipsis(if (post.imageUrl != null) 30 else 40),
                    style = AppTypography.label01,
                    color = HelloWorldGrayScale500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (post.imageUrl != null) {
                Spacer(modifier = Modifier.width(40.dp))
                AsyncImage(
                    model = post.imageUrl,
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
                text = "${post.categoryId.toCategoryName()} • ${post.createdAt.toFormattedDate()}",
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
                    text = "${post.commentNum}",
                    style = AppTypography.label03,
                    color = HelloWorldGrayScale500
                )
            }
        }
    }
}

@Composable
internal fun TabIconAndLabel(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String = "",
    onIconClick: () -> Unit,
    isSelected: Boolean = false,
) {
    Column(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onIconClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        key(isSelected) {
            Card(
                modifier = Modifier
                    .size(60.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color.White else Color(0xFFF9FBFC)
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = if (isSelected) 3.dp else 1.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                    )
                }
            }
        }
        Text(
            text = title,
            style = AppTypography.label01,
            color = if (isSelected) HelloWorldGrayScale800 else HelloWorldGrayScale300
        )
    }
}