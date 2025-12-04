package com.example.feature.ui.community

import android.content.Context
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.video.videoFrameMillis
import com.example.core.ui.R
import com.example.core.ui.component.DialogData
import com.example.core.ui.component.HWDialog
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale100
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain200
import com.example.core.ui.theme.HelloWorldMain400
import com.example.core.ui.theme.HelloWorldMain500
import com.example.core.util.extension.advancedImePadding
import com.example.core.util.rememberMultiplePhotoPickerWithPermission
import com.example.model.common.ContentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CommunityPostWrite(
    onNavigateBack: () -> Unit,
    onCommunityUpdated: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostWriteViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val selectedTab by viewModel.selectedTab.collectAsState()
    val title by viewModel.title.collectAsState()
    val content by viewModel.content.collectAsState()
    val dialogData by viewModel.dialogData.collectAsState()
    val images by viewModel.images.collectAsState()

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    val isCreate = viewModel.request.type == ContentType.CREATE

    // 권한 체크 후 갤러리 런처
    val launchPhotoPicker = rememberMultiplePhotoPickerWithPermission(
        maxItems = 10
    ) { uris ->
        viewModel.addImages(uris)
    }

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
                painter = painterResource(R.drawable.ic_keyboard_arrow_left),
                contentDescription = null,
                tint = HelloWorldMain500,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        if (title.isBlank() && content.isBlank()) {
                            onNavigateBack()
                        } else {
                            viewModel.updateDialogData(
                                DialogData(
                                    title = R.string.signup_exit_title,
                                    subTitle = R.string.signup_exit_message,
                                    dismiss = R.string.signup_exit_leave,
                                    confirm = R.string.signup_exit_continue,
                                    onDismiss = {
                                        viewModel.updateDialogData()
                                        onNavigateBack()
                                    },
                                    onConfirm = {
                                        // TODO api 추가
                                        viewModel.updateDialogData()
                                    }
                                )
                            )
                        }
                    }
                    .padding(8.dp)
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            val title = if (isCreate) "게시글 작성" else "게시글 수정"
            Text(
                text = title,
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
                    text = stringResource(R.string.community_post_category),
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
                        title = stringResource(R.string.community_category_workplace),
                        icon = painterResource(R.drawable.ic_problem),
                        onIconClick = { viewModel.changeTab(0) },
                        isSelected = selectedTab == 0,
                    )
                    TabIconAndLabel(
                        title = stringResource(R.string.community_category_visa),
                        icon = painterResource(R.drawable.ic_national),
                        onIconClick = { viewModel.changeTab(1) },
                        isSelected = selectedTab == 1,
                    )
                    TabIconAndLabel(
                        title = stringResource(R.string.community_category_insurance),
                        icon = painterResource(R.drawable.ic_medical),
                        onIconClick = { viewModel.changeTab(2) },
                        isSelected = selectedTab == 2,
                    )
                    TabIconAndLabel(
                        title = stringResource(R.string.community_category_etc),
                        icon = painterResource(R.drawable.ic_etc),
                        onIconClick = { viewModel.changeTab(3) },
                        isSelected = selectedTab == 3,
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.community_post_title),
                        style = AppTypography.label01,
                        color = HelloWorldGrayScale800,
                    )
                    Text(
                        text = "${title.length} / 50",
                        style = AppTypography.label02,
                        color = HelloWorldGrayScale500,
                    )
                }
                BasicTextField(
                    value = title,
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
                        if (title.isBlank()) {
                            Text(
                                text = stringResource(R.string.community_post_title_placeholder),
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.community_post_content),
                        style = AppTypography.label01,
                        color = HelloWorldGrayScale800,
                    )
                    Text(
                        text = "${content.length} / 2000",
                        style = AppTypography.label02,
                        color = HelloWorldGrayScale500,
                    )
                }

                BasicTextField(
                    value = content,
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
                        if (content.isBlank()) {
                            Text(
                                text = stringResource(R.string.community_post_content_placeholder),
                                style = AppTypography.body02,
                                color = HelloWorldGrayScale300,
                            )
                        }
                        innerTextField()
                    }
                }
            }
            if (isCreate) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.community_post_photo),
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
                        if (images.size < 10) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(HelloWorldGrayScale100, RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        launchPhotoPicker()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_picture),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier
                                        .size(16.dp)
                                )
                            }
                        }
                        images.forEach { imageUri ->
                            MediaContentBox(
                                context = context,
                                imageUri = imageUri,
                                onRemoveMedia = viewModel::removeImage
                            )
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
                        text = stringResource(R.string.community_post_photo_limit),
                        style = AppTypography.label03,
                        color = HelloWorldGrayScale300,
                    )
                    /*                Text(
                                        text = "영상은 최대 2개까지 업로드 가능합니다",
                                        style = AppTypography.label03,
                                        color = HelloWorldGrayScale300,
                                    )*/
                    Text(
                        text = stringResource(R.string.community_post_guideline_abuse),
                        style = AppTypography.label03,
                        color = HelloWorldGrayScale300,
                    )
                    Text(
                        text = stringResource(R.string.community_post_guideline_copyright),
                        style = AppTypography.label03,
                        color = HelloWorldGrayScale300,
                    )
                }
            }
        }
        TextButton(
            onClick = {
                focusManager.clearFocus()
                viewModel.updateDialogData(
                    DialogData(
                        title = if (isCreate) R.string.community_post_publish_confirm else R.string.community_post_edit_confirm,
                        subTitle = R.string.community_post_edit_notice,
                        dismiss = R.string.community_post_cancel,
                        confirm = if (isCreate) R.string.community_post_publish else R.string.action_edit,
                        onDismiss = { viewModel.updateDialogData() },
                        onConfirm = {
                            viewModel.updateDialogData()
                            if (isCreate) {
                                viewModel.submitCommunityPost(context, onResult = { result -> if (result) { onCommunityUpdated() } })
                            } else {
                                viewModel.updateCommunityPost { result -> if (result) { onCommunityUpdated() } }
                            }
                        }
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .advancedImePadding(),
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFFF9FBFC),
                containerColor = HelloWorldMain400,
                disabledContentColor = HelloWorldGrayScale500,
                disabledContainerColor = HelloWorldGrayScale100,
            ),
            enabled = title.isNotBlank() && content.isNotBlank() && (title.length <= 50) && (content.length <= 2000), // TODO viewmodel
            shape = RoundedCornerShape(0),
            contentPadding = PaddingValues(
                top = 22.dp,
                bottom = 36.dp,
            )
        ) {
            Text(
                text = stringResource(R.string.community_post_complete),
                style = AppTypography.heading01,
            )
        }
    }
    dialogData?.let {
        HWDialog(it)
    }
}

@Composable
private fun MediaContentBox(
    context: Context,
    imageUri: Uri,
    onRemoveMedia: (Uri) -> Unit,
) {
    val mimeType = remember(imageUri) {
        context.contentResolver.getType(imageUri)
    }

    var showDeleteIcon by remember { mutableStateOf(false) }

    Box {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUri)
                .apply {
                    if (mimeType?.startsWith("video/") == true) {
                        videoFrameMillis(500)
                    }
                }
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(50.dp)
                .clickable { showDeleteIcon = true }
        )
        if (showDeleteIcon) {
            Box(
                modifier = Modifier
                    .background(Color(0x4D0C0C0C), RoundedCornerShape(8.dp))
                    .size(50.dp)
                    .clickable {
                        onRemoveMedia(imageUri)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp),
                    tint = Color.Unspecified
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityPostWritePreview() {
    CommunityPostWrite(
        onNavigateBack = {},
        onCommunityUpdated = {},
    )
}