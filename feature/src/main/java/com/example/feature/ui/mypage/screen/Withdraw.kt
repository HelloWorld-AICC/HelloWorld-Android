package com.example.feature.ui.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.R
import com.example.core.ui.component.DialogData
import com.example.core.ui.component.HWDialog
import com.example.core.ui.component.HWDropdownMenuBox
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale100
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain0
import com.example.core.ui.theme.HelloWorldMain500
import com.example.feature.ui.mypage.viewmodel.WithdrawViewModel

@Composable
fun Withdraw(
    onNavigateBack: () -> Unit,
    onNavigateToWithdrawComplete: () -> Unit,
    viewModel: WithdrawViewModel = hiltViewModel()
) {
    val dialogData by viewModel.dialogData.collectAsState()

    // TODO viewmodel
    val options: List<String> = listOf(
        stringResource(R.string.mypage_withdraw_reason_no_feature),
        stringResource(R.string.mypage_withdraw_reason_not_helpful),
        stringResource(R.string.mypage_withdraw_reason_difficult),
        stringResource(R.string.mypage_withdraw_reason_no_work),
    )
    var selected by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                text = stringResource(R.string.withdraw),
                style = AppTypography.heading04,
                color = HelloWorldGrayScale800
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_mascot_withdraw),
                contentDescription = null,
                modifier = Modifier
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.mypage_withdraw_title),
                    style = AppTypography.title03,
                    color = HelloWorldGrayScale800,
                )
                Text(
                    text = stringResource(R.string.mypage_withdraw_warning),
                    style = AppTypography.label02,
                    color = HelloWorldGrayScale500,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(44.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.mypage_withdraw_reason_title),
                style = AppTypography.label01,
                color = HelloWorldGrayScale800,
            )
            HWDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth(),
                selectedItem = selected,
                items = options,
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                onClick = {
                    selected = it
                    expanded = false
                },
                selectedContent = { item ->
                    Text(
                        text = item ?: stringResource(R.string.mypage_withdraw_reason_placeholder),
                        style = AppTypography.heading04,
                        color = if (item == null) HelloWorldGrayScale300 else HelloWorldGrayScale800
                    )
                },
                itemContent = { item, isSelected ->
                    Text(
                        text = item,
                        style = AppTypography.heading04,
                        color = HelloWorldGrayScale500
                    )
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.withdraw),
                style = AppTypography.heading01,
                color = HelloWorldGrayScale500,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(HelloWorldGrayScale100)
                    .weight(1f)
                    .clickable(
                        enabled = selected != null
                    ) {
                        viewModel.updateDialogData(
                            DialogData(
                                title = R.string.mypage_withdraw_confirm,
                                subTitle = R.string.mypage_withdraw_rights_warning,
                                dismiss = R.string.community_post_cancel,
                                confirm = R.string.withdraw,
                                onDismiss = { viewModel.updateDialogData() },
                                onConfirm = {
                                    viewModel.updateDialogData()
                                    viewModel.withdraw { result ->
                                        if (result) {
                                            onNavigateToWithdrawComplete()
                                        } else {
                                            // TODO 실패
                                        }
                                    }
                                }
                            )
                        )
                    }
                    .padding(vertical = 22.dp)
            )
            Text(
                text = stringResource(R.string.community_post_cancel),
                style = AppTypography.heading01,
                color = HelloWorldMain0,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(HelloWorldMain500)
                    .weight(1f)
                    .clickable { onNavigateBack() }
                    .padding(vertical = 22.dp)
            )
        }
    }

    dialogData?.let {
        HWDialog(it)
    }
}

@Preview(showBackground = true)
@Composable
fun WithdrawPreview() {
    Withdraw(
        onNavigateBack = {},
        onNavigateToWithdrawComplete = {},
    )
}