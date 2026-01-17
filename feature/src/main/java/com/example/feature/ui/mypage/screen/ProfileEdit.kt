package com.example.feature.ui.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.core.ui.component.DialogData
import com.example.core.ui.component.HWDialog
import com.example.core.ui.component.HWDropdownMenuBox
import com.example.core.ui.component.HWToast
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale0
import com.example.core.ui.theme.HelloWorldGrayScale100
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain100
import com.example.core.ui.theme.HelloWorldMain400
import com.example.core.ui.theme.HelloWorldMain500
import com.example.core.util.rememberPhotoPickerWithPermission
import com.example.feature.ui.mypage.viewmodel.ProfileEditViewModel
import com.example.model.common.Language
import com.example.core.ui.R
import com.example.model.common.National

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEdit(
    onNavigateBack: () -> Unit,
    onProfileUpdated: () -> Unit,
    viewModel: ProfileEditViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val initUser = viewModel.initUser
    val editingNickName by viewModel.editingNickName.collectAsState()
    val editingUserImg by viewModel.editingUserImg.collectAsState()
    val editingLanguage by viewModel.editingLanguage.collectAsState()
    val dialogData by viewModel.dialogData.collectAsState()
    val toastData by viewModel.toastData.collectAsState()

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    val languages: List<Language> = Language.entries
    var expanded by remember { mutableStateOf(false) }

    // 권한 체크 후 갤러리 런처
    val launchPhotoPicker = rememberPhotoPickerWithPermission { uri ->
        viewModel.updateUserImg(uri)
    }

    Column(
        modifier = Modifier
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
                        if (editingNickName.isBlank()) {
                            onNavigateBack()
                        } else {
                            viewModel.updateDialogData(
                                DialogData(
                                    title = R.string.signup_exit_title,
                                    subTitle = R.string.signup_exit_message,
                                    dismiss = R.string.signup_exit_leave,
                                    confirm = R.string.action_edit,
                                    onDismiss = {
                                        viewModel.updateDialogData()
                                        onNavigateBack()
                                    },
                                    onConfirm = { viewModel.updateDialogData() }
                                )
                            )
                        }
                    }
                    .padding(8.dp)
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.mypage_profile_edit),
                style = AppTypography.heading04,
                color = HelloWorldGrayScale800
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 38.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clickable {
                        launchPhotoPicker()
                    }
            ) {
                if (editingUserImg != null) {
                    AsyncImage(
                        model = editingUserImg,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                } else if (initUser.userImg != null) {
                    AsyncImage(
                        model = initUser.userImg,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.ic_mascot_normal_profile),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(Color(0x4D0C0C0C))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_picture),
                        contentDescription = null,
                        tint = HelloWorldGrayScale0,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(16.dp)
                    )
                }
                Image(
                    painter = when (initUser.language?.national) {
                        National.KOREAN -> painterResource(R.drawable.flag_kr)
                        National.JAPANESE -> painterResource(R.drawable.flag_jp)
                        National.CHINESE -> painterResource(R.drawable.flag_ch)
                        National.VIETNAMESE -> painterResource(R.drawable.flag_vi)
                        else -> painterResource(R.drawable.flag_en)
                    },
                    contentDescription = "국기",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                )
            }
            Text(
                text = initUser.name,
                style = AppTypography.heading01,
                color = HelloWorldGrayScale800,
            )
        }
        Column(
            modifier = Modifier
                .padding(top = 32.dp)
                .padding(horizontal = 24.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.mypage_nickname_edit),
                    style = AppTypography.label01,
                    color = HelloWorldGrayScale800,
                )
                BasicTextField(
                    value = editingNickName,
                    onValueChange = { viewModel.updateNickname(it) },
                    singleLine = true,
                    textStyle = AppTypography.heading04.copy(color = HelloWorldGrayScale800),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(2.dp, HelloWorldMain100, RoundedCornerShape(8.dp))
                        .background(HelloWorldGrayScale0, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                ) { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (editingNickName.isBlank()) {
                            Text(
                                text = initUser.name,
                                style = AppTypography.heading04,
                                color = HelloWorldGrayScale300,
                            )
                        }
                        innerTextField()
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.mypage_language_change),
                    style = AppTypography.label01,
                    color = HelloWorldGrayScale800,
                )
                HWDropdownMenuBox(
                    modifier = Modifier
                        .fillMaxWidth(),
                    selectedItem = if (editingLanguage == null) initUser.language else editingLanguage,
                    items = languages,
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    onClick = {
                        viewModel.updateLanguage(it)
                        expanded = false
                    },
                    selectedContent = { item ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = when (item?.national) {
                                    National.KOREAN -> painterResource(R.drawable.flag_kr)
                                    National.JAPANESE -> painterResource(R.drawable.flag_jp)
                                    National.CHINESE -> painterResource(R.drawable.flag_ch)
                                    National.VIETNAMESE -> painterResource(R.drawable.flag_vi)
                                    else -> painterResource(R.drawable.flag_en)
                                },
                                contentDescription = "국기",
                                modifier = Modifier
                                    .size(24.dp)
                            )
                            Text(
                                text = "${item?.displayName}",
                                style = AppTypography.heading04,
                                color = HelloWorldGrayScale800,
                            )
                        }
                    },
                    itemContent = { item, isSelected ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = when (item.national) {
                                    National.KOREAN -> painterResource(R.drawable.flag_kr)
                                    National.JAPANESE -> painterResource(R.drawable.flag_jp)
                                    National.CHINESE -> painterResource(R.drawable.flag_ch)
                                    National.VIETNAMESE -> painterResource(R.drawable.flag_vi)
                                    else -> painterResource(R.drawable.flag_en)
                                },
                                contentDescription = "국기",
                                modifier = Modifier
                                    .size(24.dp)
                            )
                            Text(
                                text = item.displayName,
                                style = AppTypography.heading04,
                                color = HelloWorldGrayScale800,
                            )
                        }
                    }
                )
            }
        }
        TextButton(
            onClick = {
                viewModel.setProfile(
                    context = context,
                    onResult = { success -> if (success) { onProfileUpdated() } }
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
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
            ),
            enabled = editingNickName.length <= 15
        ) {
            Text(
                text = stringResource(R.string.confirm),
                style = AppTypography.heading01,
            )
        }
    }
    dialogData?.let {
        HWDialog(it)
    }
    toastData?.let {
        HWToast(it)
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileEditPreview() {
    ProfileEdit(
        onNavigateBack = {},
        onProfileUpdated = {}
    )
}