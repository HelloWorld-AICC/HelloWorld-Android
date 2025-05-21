package com.example.feature.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale200
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain200



//import com.example.core.util.extension.truncateWithEllipsis

import kotlinx.serialization.Serializable



import com.example.core.util.extension.truncateWithEllipsis



@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Community(
    onWriteClick: () -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val selectedTab = viewModel.selectedTab.collectAsState()
    val problemPosts = viewModel.problemPosts.collectAsState()
    val nationalPosts = viewModel.nationalPosts.collectAsState()
    val medicalPosts = viewModel.medicalPosts.collectAsState()
    val etcPosts = viewModel.etcPosts.collectAsState()

    val tabIndex = when (selectedTab.value) {
        "problem" -> 0
        "national" -> 1
        "medical" -> 2
        "etc" -> 3
        else -> 0
    }
    val pagerState = rememberPagerState(
        initialPage = tabIndex,
        pageCount = { 4 }
    )
    LaunchedEffect(pagerState.currentPage) {
        when (pagerState.currentPage) {
            0 -> viewModel.changeTab("problem")
            1 -> viewModel.changeTab("national")
            2 -> viewModel.changeTab("medical")
            3 -> viewModel.changeTab("etc")
        }
    }
    LaunchedEffect(selectedTab.value) {
        pagerState.animateScrollToPage(tabIndex)
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
            HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = HelloWorldMain200)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> CommunityTabContent(
                        posts = problemPosts.value,
                        onPostClick = { onPostClick() },
                    )
                    1 -> CommunityTabContent(
                        posts = nationalPosts.value,
                        onPostClick = { onPostClick() },
                    )
                    2 -> CommunityTabContent(
                        posts = medicalPosts.value,
                        onPostClick = { onPostClick() },
                    )
                    3 -> CommunityTabContent(
                        posts = etcPosts.value,
                        onPostClick = { onPostClick() },
                    )
                }
            }
        }
        // FAB
        IconButton(
            onClick = { onWriteClick() },
            modifier = Modifier
                .padding(18.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                Icons.Default.Create,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}

@Composable
private fun CommunityTabContent(
    posts: List<CommunityPost>,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (posts.isNotEmpty()) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            items(posts) { post ->
                CommunityPostItem(
                    post = post,
                    onPostClick = onPostClick
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable { onPostClick() },
            contentAlignment = Alignment.Center
        ) {
            // TODO (Bro) image 로 수정
            Icon(
                Icons.Default.Face,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun CommunityPostItem(
    post: CommunityPost,
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

//                Text(
//                    text = post.title.truncateWithEllipsis(20),
//                    style = AppTypography.body02,
//                    color = HelloWorldGrayScale800,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = post.content.truncateWithEllipsis(if (post.thumbnail) 30 else 40),
//                    style = AppTypography.label01,
//                    color = HelloWorldGrayScale500,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                )

                Text(
                    text = post.title.truncateWithEllipsis(20),
                    style = AppTypography.body02,
                    color = HelloWorldGrayScale800,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.content.truncateWithEllipsis(if (post.thumbnail) 30 else 40),
                    style = AppTypography.label01,
                    color = HelloWorldGrayScale500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

            }
            if (post.thumbnail) {
                Spacer(modifier = Modifier.width(40.dp))
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(HelloWorldGrayScale200)
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
                text = "${post.category} • ${post.date}",
                style = AppTypography.label03,
                color = HelloWorldGrayScale500
            )
            // 댓글
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.MailOutline,
                    contentDescription = null,
                    modifier = Modifier
                        .size(12.dp),
                    tint = HelloWorldGrayScale500
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = post.commentCount,
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
    icon: ImageVector,
    title: String = "",
    onIconClick: () -> Unit,
    isSelected: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = { onIconClick() }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        }
        Text(
            text = title,
            style = AppTypography.label01,
            color = if (isSelected) HelloWorldGrayScale800 else HelloWorldGrayScale300
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityPreview() {
    Community(
        onWriteClick = {},
        onPostClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PostItemAndImagePreview() {
    CommunityPostItem(
        post = CommunityPost(
            id = "1",
            title = "월급이 제대로 안 들어 온 것 같아요",
            content = "이번 달 일을 했는데 제가 계산한 돈과 월급이 다르게 들어 온 것 같아요",
            category = "problem",
            date = "25.05.06",
            commentCount = "30",
            thumbnail = true
        ),
        onPostClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PostItemAndPreview() {
    CommunityPostItem(
        post = CommunityPost(
            id = "1",
            title = "월급이 제대로 안 들어 온 것 같아요",
            content = "이번 달 일을 했는데 제가 계산한 돈과 월급이 다르게 들어 온 것 같아요",
            category = "problem",
            date = "25.05.06",
            commentCount = "30",
            thumbnail = false
        ),
        onPostClick = {},
    )
}

data class CommunityPost(
    val id: String,
    val title: String,
    val content: String,
    val category: String,
    val date: String,
    val commentCount: String,
    val thumbnail: Boolean, // TODO (Bro) Thumbnail
)