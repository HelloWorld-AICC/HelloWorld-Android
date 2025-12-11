package com.example.feature.ui.onboarding.screen

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.core.ui.component.DialogData
import com.example.core.ui.component.HWDialog
import com.example.core.ui.components.BottomButton
import com.example.core.ui.theme.*
import com.example.feature.R
import com.example.core.ui.R as languageR
import kotlinx.coroutines.launch
import kotlin.String

@Composable
fun AgreementScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context as? Activity

    var agreeTerms by remember { mutableStateOf(false) }
    var agreePrivacy by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }
    var shouldNavigateToHome by remember { mutableStateOf(false) }

    val allAgree = agreeTerms && agreePrivacy

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = !showModal) {
        showExitDialog = true
    }

    LaunchedEffect(shouldNavigateToHome) {
        if (shouldNavigateToHome) {
            navController.navigate("축하") {
                popUpTo("이용 동의") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomButton(
                text = stringResource(languageR.string.confirm),
                onClick = { showModal = true },
                enabled = agreePrivacy && agreeTerms,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Image(
                painter = painterResource(id = R.drawable.agreement_character),
                contentDescription = "agreement character",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(languageR.string.signup_welcome_title),
                style = AppTypography.heading01,
                color = HelloWorldGrayScale800
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(languageR.string.signup_terms_title),
                style = AppTypography.body02,
                color = HelloWorldGrayScale300
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AgreementCheckbox(
                    text = stringResource(languageR.string.signup_terms_agree_all),
                    checked = allAgree,
                    onCheckedChange = { isChecked ->
                        agreeTerms = isChecked
                        agreePrivacy = isChecked
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                AgreementCheckbox(
                    text = "개인정보 처리방침",
                    checked = agreePrivacy,
                    onCheckedChange = { isChecked ->
                        agreePrivacy = isChecked
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                AgreementCheckbox(
                    text = "서비스 이용약관",
                    checked = agreeTerms,
                    onCheckedChange = { isChecked ->
                        agreeTerms = isChecked
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(languageR.string.signup_terms_agree_all_notice),
                    style = AppTypography.label03,
                    color = HelloWorldGrayScale300
                )
            }
        }
    }

    if (showModal) {
        TermsModal(
            onDismiss = { isAgreed ->
                showModal = false
                if (isAgreed) {
                    shouldNavigateToHome = true
                }
            }
        )
    }

    if (showExitDialog) {
        HWDialog(
            data = DialogData(
                title = languageR.string.signup_exit_title,
                subTitle = languageR.string.signup_exit_message,
                dismiss = languageR.string.signup_exit_leave,
                confirm = languageR.string.signup_exit_continue,
                onDismiss = {
                    showExitDialog = false
                    activity?.finish()
                },
                onConfirm = {
                    showExitDialog = false
                },
            )
        )
    }
}

@Composable
fun AgreementCheckbox(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    OutlinedButton(
        onClick = { onCheckedChange(!checked) },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .border(
                width = 1.dp,
                color = HelloWorldMain400,
                shape = RoundedCornerShape(8.dp)
            ),
        border = BorderStroke(1.dp, HelloWorldMain100),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = AppTypography.heading04,
                color = HelloWorldGrayScale700
            )

            RadioButton(
                selected = checked,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = HelloWorldMain500,
                    unselectedColor = HelloWorldGrayScale300
                )
            )
        }
    }
}

@Composable
fun TermsModal(onDismiss: (Boolean) -> Unit) {
    val scrollState = rememberScrollState()
    val agreed = remember { mutableStateOf(false) }
    val hasScrolledToBottom = remember {
        derivedStateOf {
            scrollState.maxValue > 0 && scrollState.value == scrollState.maxValue
        }
    }

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { onDismiss(false) },
        confirmButton = {
            if (hasScrolledToBottom.value) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = agreed.value,
                        onClick = {
                            agreed.value = true
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(100)
                                onDismiss(true)
                            }
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = HelloWorldMain500,
                            unselectedColor = HelloWorldGrayScale300
                        )
                    )
                    Text(
                        text = "약관 내용을 확인하고 동의합니다",
                        style = AppTypography.label01,
                        color = HelloWorldGrayScale800,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .height(400.dp)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = "info icon",
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(languageR.string.terms_privacy),
                    style = AppTypography.heading01,
                    color = HelloWorldGrayScale800
                )

                Text(
                    text = stringResource(languageR.string.privacy_policy_full),
                    style = AppTypography.label02,
                    color = HelloWorldGrayScale500
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}