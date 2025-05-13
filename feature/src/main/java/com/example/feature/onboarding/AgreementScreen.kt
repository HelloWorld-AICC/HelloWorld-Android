package com.example.feature.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.theme.*
import com.example.core.ui.theme.Pretendard
import com.example.feature.R
import kotlinx.coroutines.launch

@Composable
fun AgreementScreen() {
    var allAgree by remember { mutableStateOf(false) }
    var agreeTerms by remember { mutableStateOf(false) }
    var agreePrivacy by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }

    LaunchedEffect(allAgree) {
        if (allAgree) {
            agreeTerms = true
            agreePrivacy = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.language_character),
            contentDescription = "agreement character",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "HelloWorld와 함께해요",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Pretendard,
            color = Gray800
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "서비스 이용을 위한 약관에 동의해 주세요",
            fontSize = 14.sp,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            color = Gray300
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            AgreementCheckbox(text = "모두 동의", checked = allAgree, onCheckedChange = {
                allAgree = it
            })

            Spacer(modifier = Modifier.height(12.dp))

            AgreementCheckbox(text = "개인정보 처리방침", checked = agreePrivacy, onCheckedChange = {
                agreePrivacy = it
                allAgree = agreePrivacy && agreeTerms
            })

            Spacer(modifier = Modifier.height(12.dp))

            AgreementCheckbox(text = "서비스 이용약관", checked = agreeTerms, onCheckedChange = {
                agreeTerms = it
                allAgree = agreePrivacy && agreeTerms
            })
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { showModal = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (agreePrivacy && agreeTerms) Main500 else Gray300
            ),
            enabled = agreePrivacy && agreeTerms
        ) {
            Text(
                text = "확인",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showModal) {
        TermsModal(onDismiss = { showModal = false })
    }
}

@Composable
fun AgreementCheckbox(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    OutlinedButton(
        onClick = { onCheckedChange(!checked) },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        border = BorderStroke(1.dp, Main100),
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
                fontFamily = Pretendard,
                fontSize = 14.sp,
                color = Gray800
            )

            RadioButton(
                selected = checked,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Main500,
                    unselectedColor = Gray300
                )
            )
        }
    }
}

@Composable
fun TermsModal(onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()
    val agreed = remember { mutableStateOf(false) }
    val hasScrolledToBottom = remember { derivedStateOf {
        scrollState.maxValue > 0 && scrollState.value == scrollState.maxValue
    } }

    val coroutineScope = rememberCoroutineScope()


    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (hasScrolledToBottom.value) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = agreed.value,
                        onClick = {
                            agreed.value = true
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(100)
                                onDismiss()
                            }
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Main500,
                            unselectedColor = Gray300
                        )
                    )
                    Text(
                        text = "약관 내용을 확인하고 동의합니다",
                        fontFamily = Pretendard,
                        fontSize = 14.sp,
                        color = Gray800,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier
                .height(400.dp)
                .verticalScroll(scrollState)
                .padding(8.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = "info icon",
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "개인정보 처리방침",
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = Gray800
                )

                Text(
                    text = "\n개인정보 처리방침은 HelloWorld 서비스 제공 개발팀(이하 \"당사\"라 합니다)이 운영하는 가입절차를 거친 이용자를 위한 이용 가능형 서비스 플랫폼에 있어, 개인정보를 어떻게 수집, 이용, 보관, 파기하는지에 대해 정보를 담은 방침을 의미합니다. 개인정보 처리방침은 개인정보보호법 등 국내 개인정보 보호 법령을 모두 준수하고 있습니다. 본 개인정보 처리방침에서 정하지 않은 용어의 정의는 서비스 이용약관을 따릅니다.\n\n수집하는 개인정보의 항목\n\n팀은 서비스 제공을 위해 다음 항목 중 최소한의 개인정보를 수집합니다.\n\n1. 회원가입 시 수집하는 개인정보\n- 카카오톡 계정 정보, 닉네임\n1. 별도로 수집하는 개인정보\n1) 프로필 사진을 저장할 경우\n- 프로필 사진\n\n  작성되었습니다. 그 다음 내용 여기까지 오게 해주세요 그 다음 내용 여기까지 오게 해주세요그 다음 내용 여기까지 오게 해주세요그 다음 내용 여기까지 오게 해주세요그 다음 내용 여기까지 오게 해주세요 다음 내용 여기까지 오게 해주세요 다음 내용 여기까지 오게 해주세요 다음 내용 여기까지 오게 해주세요 다음 내용 여기까지 오게 해주세요 다음 내용 여기까지 오게 해주세요 다음 내용 여기까지 오게 해주세요 다음 내용 여기까지 오게 해주세요 다음 내용 여기까지 오게 해주세요 다음 내용 여기까지 오게 해주세요 다음 내용 여기까지 오게 해주세요 다음 내용 여기까지 오게 해주세요 다음 내용 여기까지 오게 해주세요.",
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Gray500
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}
