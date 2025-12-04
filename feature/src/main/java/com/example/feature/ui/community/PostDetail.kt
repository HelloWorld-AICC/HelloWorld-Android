package com.example.feature.ui.community

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import coil3.compose.AsyncImage
import com.example.core.ui.R
import com.example.core.ui.component.DialogData
import com.example.core.ui.component.HWDialog
import com.example.core.ui.component.HWToast
import com.example.core.ui.component.ToastData
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldError
import com.example.core.ui.theme.HelloWorldGrayScale100
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain200
import com.example.core.ui.theme.HelloWorldMain500
import com.example.core.util.extension.advancedImePadding
import com.example.core.util.extension.toCategoryName
import com.example.core.util.extension.toFormattedDate
import com.example.model.common.ContentType
import com.example.model.community.CommunityDetailFile
import com.example.model.community.DetailComment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CommunityPostDetail(
    onNavigateToCommunityPostWrite: (Int, Int, ContentType) -> Unit,
    onNavigateBack: () -> Unit,
    onCommunityUpdated: () -> Unit,
    onCheckCommunityUpdate: () -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        val isUpdate = onCheckCommunityUpdate()

        if (isUpdate) {
            viewModel.getContent(true)
            onCommunityUpdated()
        }
    }
    val post by viewModel.post.collectAsState()
    val commentList by viewModel.commentList.collectAsState()
    val commentText by viewModel.commentText.collectAsState()

    val showMediaViewer by viewModel.showMediaViewer.collectAsState()

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    var expanded by remember { mutableStateOf(false) }
    val dialogData by viewModel.dialogData.collectAsState()
    val toastData by viewModel.toastData.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()

    BackHandler(enabled = showMediaViewer.visible) {
        viewModel.visibleMediaViewer(
            data = MediaViewData(visible = false)
        )
    }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(lazyListState, commentList.size) {
        snapshotFlow { lazyListState.layoutInfo }
            .map { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItemsCount = layoutInfo.totalItemsCount

                lastVisibleItemIndex >= totalItemsCount - 2
            }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore && !isLoading && commentList.isNotEmpty()) {
                    viewModel.getContent()
                }
            }
    }

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
                    text = stringResource(R.string.home_community_title),
                    style = AppTypography.heading04,
                    color = HelloWorldGrayScale800
                )
            }
            Box {
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
                    onDismissRequest = { expanded = false },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (post.isOwner) {
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(R.string.action_edit),
                                style = AppTypography.label02,
                                color = HelloWorldGrayScale500,
                            ) },
                            onClick = {
                                expanded = false
                                onNavigateToCommunityPostWrite(
                                    viewModel.request.categoryId.toInt(),
                                    viewModel.request.communityId.toInt(),
                                    ContentType.UPDATE
                                )
                            },
                            modifier = Modifier
                                .height(24.dp)
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(R.string.action_delete),
                                style = AppTypography.label02,
                                color = HelloWorldGrayScale500,
                            ) },
                            onClick = {
                                expanded = false
                                viewModel.updateDialogData(
                                    DialogData(
                                        title = R.string.community_delete_post_confirm,
                                        subTitle = R.string.community_delete_post_warning,
                                        dismiss = R.string.action_back,
                                        confirm = R.string.action_delete,
                                        onDismiss = { viewModel.updateDialogData() },
                                        onConfirm = {
                                            viewModel.updateDialogData()
                                            viewModel.deletePost { result ->
                                                if (result) {
                                                    onCommunityUpdated()
                                                    onNavigateBack()
                                                } else {
                                                    // TODO 실패처리
                                                }
                                            }
                                        },
                                    )
                                )
                            },
                            modifier = Modifier
                                .height(24.dp)
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(R.string.action_report),
                                style = AppTypography.label02,
                                color = HelloWorldError,
                            ) },
                            onClick = {
                                expanded = false
                                viewModel.updateDialogData(
                                    DialogData(
                                        title = R.string.community_report_post_confirm,
                                        subTitle = R.string.community_report_warning,
                                        dismiss = R.string.action_back,
                                        confirm = R.string.action_report,
                                        onDismiss = { viewModel.updateDialogData() },
                                        onConfirm = {
                                            // TODO api 추가
                                            viewModel.updateDialogData()
                                            viewModel.reportPost { result ->
                                                if (result) {
                                                    viewModel.updateToastData(
                                                        ToastData(
                                                            text = R.string.community_report_post_complete,
                                                            onDismiss = { viewModel.updateToastData() }
                                                        )
                                                    )
                                                } else {
                                                    viewModel.updateToastData(
                                                        ToastData(
                                                            text = 0, // 서버와의 통신에 실패했습니다.
                                                            onDismiss = { viewModel.updateToastData() }
                                                        )
                                                    )
                                                }
                                            }
                                        },
                                    )
                                )
                            },
                            modifier = Modifier
                                .height(24.dp)
                        )
                    }
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
                .background(Color.White),
            state = lazyListState
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
                            text = "${viewModel.request.categoryId.toCategoryName()} • ${post.createdAt.toFormattedDate()}",
                            style = AppTypography.label02,
                            color = HelloWorldGrayScale500,
                        )
                        Text(
                            text = post.title,
                            style = AppTypography.heading02,
                            color = HelloWorldGrayScale800,
                        )
                    }
                    Text(
                        text = post.content,
                        style = AppTypography.body02,
                        color = HelloWorldGrayScale500,
                    )
                    if (post.fileList.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .height(100.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(post.fileList) { index, file ->
                                val indexNum = if (index < 9) {
                                    "0${index+1}"
                                } else {
                                    "${index+1}"
                                }
                                val pageSize = if (post.fileList.size < 10) {
                                    "0${post.fileList.size}"
                                } else {
                                    "${post.fileList.size}"
                                }
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clickable {
                                            viewModel.visibleMediaViewer(
                                                MediaViewData(
                                                    visible = true,
                                                    pageNum = index
                                                )
                                            )
                                        }
                                ) {
                                    AsyncImage(
                                        model = file.fileUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop,
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(start = 4.dp, bottom = 4.dp)
                                    ) {
                                        Text(
                                            text = "${indexNum}/${pageSize}",
                                            style = AppTypography.carousel,
                                            color = HelloWorldGrayScale100,
                                            modifier = Modifier
                                                .background(Color(0x4D0C0C0C), CircleShape)
                                                .padding(horizontal = 2.dp, vertical = 1.dp)
                                        )
                                    }
                                }
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
            if (commentList.isNotEmpty()) {
                items(commentList) { comment ->
                    CommentItem(
                        comment = comment,
                        onDeleteClick = {
                            viewModel.updateDialogData(
                                DialogData(
                                    title = R.string.community_delete_comment_confirm,
                                    subTitle = R.string.community_delete_comment_warning,
                                    dismiss = R.string.action_back,
                                    confirm = R.string.action_delete,
                                    onDismiss = { viewModel.updateDialogData() },
                                    onConfirm = {
                                        viewModel.updateDialogData()
                                        viewModel.deleteComment(comment.commentId) { result ->
                                            if (result) {
                                                viewModel.updateToastData(
                                                    ToastData(
                                                        text = R.string.community_delete_comment_complete,
                                                        onDismiss = {
                                                            viewModel.updateToastData()
                                                            onCommunityUpdated()
                                                        }
                                                    )
                                                )
                                            } else {
                                                viewModel.updateToastData(
                                                    ToastData(
                                                        text = 0, // 댓글 삭제에 실패 했습니다.
                                                        onDismiss = { viewModel.updateToastData() }
                                                    )
                                                )
                                            }
                                        }
                                    },
                                )
                            )
                        },
                        onReportClick = {
                            viewModel.updateDialogData(
                                DialogData(
                                    title = R.string.community_report_comment_confirm,
                                    subTitle = R.string.community_report_warning,
                                    dismiss = R.string.action_back,
                                    confirm = R.string.action_report,
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
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
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
                value = commentText,
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
                    if (commentText.isBlank()) {
                        Text(
                            text = stringResource(R.string.community_input_placeholder),
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
                        tint = if (commentText.isBlank()) HelloWorldGrayScale100 else Color.Unspecified,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .clickable(
                                enabled = commentText.isNotBlank() && commentText.length <= 500
                            ) {
                                viewModel.submitComment()
                                focusManager.clearFocus()
                                onCommunityUpdated()
                            }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
    dialogData?.let {
        HWDialog(it)
    }

    toastData?.let {
        HWToast(it)
    }

    if (showMediaViewer.visible && post.fileList.isNotEmpty()) {
        MediaViewer(
            onDismiss = { viewModel.visibleMediaViewer(MediaViewData(visible = false)) },
            medias = post.fileList,
            initialPage = showMediaViewer.pageNum
        )
    }
}

@Composable
private fun CommentItem(
    modifier: Modifier = Modifier,
    comment: DetailComment,
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
                    text = "익명${comment.anonymousName}",
                    style = AppTypography.label03,
                    color = HelloWorldGrayScale800,
                )
                Text(
                    text = "•",
                    style = AppTypography.label03,
                    color = HelloWorldGrayScale300
                )
                Text(
                    text = comment.createdAt.toFormattedDate(),
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
                    onDismissRequest = { expanded = false },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (comment.isOwner) {
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(R.string.action_delete),
                                style = AppTypography.label02,
                                color = HelloWorldGrayScale500,
                            ) },
                            onClick = {
                                onDeleteClick()
                                expanded = false
                            },
                            modifier = Modifier
                                .height(24.dp)
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(R.string.action_report),
                                style = AppTypography.label02,
                                color = HelloWorldError,
                            ) },
                            onClick = {
                                onReportClick()
                                expanded = false
                            },
                            modifier = Modifier
                                .height(24.dp)
                        )
                    }
                }
            }
        }
        Text(
            text = comment.content,
            style = AppTypography.label02,
            color = HelloWorldGrayScale500
        )
    }
}

@Composable
private fun MediaViewer(
    onDismiss: () -> Unit,
    medias: List<CommunityDetailFile>,
    initialPage: Int = 0,
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { medias.size }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x4D0C0C0C))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 40.dp),
            pageSpacing = 12.dp
        ) { index ->
            Box(
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { }
            ) {
                /*VideoPlayer(
                    videoUrl = medias[index].fileUrl,
                    isPlaying = pagerState.currentPage == index,
                    modifier = Modifier
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(12.dp)),
                )*/
                AsyncImage(
                    model = medias[index].fileUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.FillWidth
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 12.dp, bottom = 12.dp)
                ) {
                    val pageNum = if (pagerState.currentPage < 9) {
                        "0${pagerState.currentPage + 1}"
                    } else {
                        "${pagerState.currentPage + 1}"
                    }
                    val pageSize = if (medias.size < 10) {
                        "0${medias.size}"
                    } else {
                        "${medias.size}"
                    }
                    Text(
                        text = "$pageNum/$pageSize",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        color = HelloWorldGrayScale100,
                        modifier = Modifier
                            .background(Color(0x4D0C0C0C), CircleShape)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoPlayer(
    videoUrl: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var videoAspectRatio by remember { mutableFloatStateOf(16f / 9f) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = false
            repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    LaunchedEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.width > 0 && videoSize.height > 0) {
                    videoAspectRatio = videoSize.width.toFloat() / videoSize.height.toFloat()
                }
            }
        }
        exoPlayer.addListener(listener)
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            exoPlayer.seekTo(0) // 0초부터 시작
            exoPlayer.playWhenReady = true
        } else {
            exoPlayer.playWhenReady = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    PlayerSurface(
        player = exoPlayer,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(videoAspectRatio)
    )
}

@Preview(showBackground = true)
@Composable
private fun CommunityPostDetailPreview() {
    CommunityPostDetail(
        onNavigateToCommunityPostWrite = {_, _, _ ->},
        onNavigateBack = {},
        onCommunityUpdated = {},
        onCheckCommunityUpdate = { false }
    )
}