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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.component.DialogData
import com.example.core.ui.component.HWDialog
import com.example.core.ui.component.HWDropdownMenuBox
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale0
import com.example.core.ui.theme.HelloWorldGrayScale100
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain100
import com.example.core.ui.theme.HelloWorldMain400
import com.example.core.ui.theme.HelloWorldMain500
import com.example.feature.R
import com.example.feature.ui.mypage.viewmodel.ProfileEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEdit(
    onNavigateBack: () -> Unit,
    viewModel: ProfileEditViewModel = hiltViewModel()
) {
    val newNickname by viewModel.newNickname.collectAsState()
    val dialogData by viewModel.dialogData.collectAsState()

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    // TODO viewmodel
    val options: List<String> = listOf("대한민국", "United States", "日本", "中國")
    var selectedLanguage by remember { mutableStateOf(options[0]) }
    var expanded by remember { mutableStateOf(false) }

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
                        if (newNickname.isBlank()) {
                            onNavigateBack()
                        } else {
                            viewModel.updateDialogData(
                                DialogData(
                                    title = "앗, 잠시만요!",
                                    subTitle = "지금 나가시면 입력한 정보는 모두 지워집니다.",
                                    dismiss = "나가기",
                                    confirm = "수정하기",
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
                text = "프로필 변경",
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
            Box {
                Image(
                    painter = painterResource(R.drawable.ic_mascot_normal_profile),
                    contentDescription = null,
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(Color(0x4D0C0C0C))
                        .clickable {}
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
                Icon(
                    Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp),
                )
            }
            Text(
                text = "NaYeEun",
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
                    text = "닉네임 변경",
                    style = AppTypography.label01,
                    color = HelloWorldGrayScale800,
                )
                BasicTextField(
                    value = newNickname,
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
                        if (newNickname.isBlank()) {
                            Text(
                                text = "Na YeEun",
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
                    text = "언어 변경",
                    style = AppTypography.label01,
                    color = HelloWorldGrayScale800,
                )
                HWDropdownMenuBox(
                    modifier = Modifier
                        .fillMaxWidth(),
                    selectedItem = selectedLanguage,
                    items = options,
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    onClick = {
                        selectedLanguage = it
                        expanded = false
                    },
                    selectedContent = { item ->
                        // TODO Row 로 수정, + Image
                        Text(
                            text = item.toString(),
                            style = AppTypography.heading04,
                            color = HelloWorldGrayScale800,
                        )
                    },
                    itemContent = { item, isSelected ->
                        // TODO Row 로 수정, + Image
                        Text(
                            text = item,
                            style = AppTypography.heading04,
                            color = HelloWorldGrayScale800,
                        )
                    }
                )
            }
        }
        TextButton(
            onClick = {  },
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
            enabled = newNickname.length <= 15
        ) {
            Text(
                text = "완료",
                style = AppTypography.heading01,
            )
        }
    }
    dialogData?.let {
        HWDialog(it)
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileEditPreview() {
    ProfileEdit(
        onNavigateBack = {}
    )
}