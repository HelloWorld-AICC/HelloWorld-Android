package com.example.feature.ui.aichat.chatting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.component.BackHeader
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale100
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldMain0
import com.example.core.ui.theme.HelloWorldMain100
import com.example.core.ui.theme.HelloWorldMain200
import com.example.core.ui.theme.HelloWorldMain400
import com.example.core.ui.theme.HelloWorldMain500
import com.example.core.ui.theme.HelloWorldMain700
import com.example.feature.R

@Composable
internal fun RecentChattingScreen(
    chatId: Int,
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    // 👇 여기서 초기화
    LaunchedEffect(chatId) {
        viewModel.selectChat(chatId)
    }

    val selectedChatId by viewModel.selectedChatId.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()

    var userInput by remember { mutableStateOf("") }

    val messages = selectedChatId?.let { chatMessages[it].orEmpty() } ?: emptyList()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
            }
    ) {
        BackHeader(
            title = "AI Chat",
            onBackClick = onBackClick
        )
        HorizontalDivider(color = HelloWorldMain200)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            reverseLayout = true
        ) {

            if (isTyping) {
                item {
                    TypingBubble()  // 👈 점 3개 말풍선 표시
                }
            }

            items(messages.reversed()) { msg ->
                ChatBubble(msg, viewModel)
            }
        }

        Row(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, HelloWorldGrayScale100, RoundedCornerShape(8.dp))
                    .fillMaxWidth()
            ) {
                BasicTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    textStyle = AppTypography.label02,
                    maxLines = 6,
                    keyboardOptions  = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (userInput.isNotBlank() && selectedChatId != null) {
                                viewModel.sendUserMessage(selectedChatId!!, userInput)
                                userInput = ""
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 44.dp)       // 보내기 아이콘 자리 확보
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                )

                /* placeholder */
                if (userInput.isEmpty()) {
                    Text(
                        text  = "메시지를 입력하세요",
                        style = AppTypography.label02,
                        color = HelloWorldGrayScale300,
                        modifier = Modifier
                            .padding(start = 12.dp, top = 8.dp)
                    )
                }

                /* 보내기 버튼 */
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_up),
                    contentDescription = "Send",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(6.dp)
                        .clickable(
                            enabled = userInput.isNotBlank(),
                            onClick = {
                                if (userInput.isNotBlank() && selectedChatId != null) {
                                    viewModel.sendUserMessage(selectedChatId!!, userInput)
                                    userInput = ""
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            }
                        ),
                    colorFilter = if (userInput.isNotBlank()) {
                        // 활성화된 상태일 때 기본 색상 또는 원하는 색상 설정
                        ColorFilter.tint(HelloWorldMain500)  // 예시로 HelloWorldMain500 색상
                    } else {
                        // 비활성화된 상태일 때 색상 변경
                        ColorFilter.tint(HelloWorldGrayScale100)  // 예시로 비활성화 색상
                    }
                )
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage, viewModel : ChatViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
        ) {
            // 말풍선 본체
            Box(
                modifier = Modifier
                    .background(
                        if (msg.isUser) HelloWorldMain500 else HelloWorldMain100,
                        RoundedCornerShape(8.dp)
                    )
                    .widthIn(max = 290.dp) // 💡 최대 너비 제한
                    .padding(horizontal = 16.dp, vertical = 11.dp)
            ) {
                Text(
                    msg.text,
                    style = AppTypography.body01,
                    color = if (msg.isUser) HelloWorldMain0 else HelloWorldMain700
                )
            }

            if (!msg.isUser && msg.showSummaryIcon) {
                Image(
                    painter = painterResource(id = R.drawable.ic_summary),
                    contentDescription = "Summarize",
                    modifier = Modifier
                        .size(20.dp)
                        .offset(x = 23.dp)
                        .clickable {
                            showDialog =viewModel.summarizeMessage(msg.text)
                        }
                )
            }

            // ✅ 분리한 다이얼로그 사용
            if (showDialog) {
                SummaryCompletedDialog(onDismiss = { showDialog = false })
            }
        }
    }
}

@Composable
fun TypingBubble() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_ai_chat_inprogress),
            contentDescription = "chat_inprogress",
            modifier = Modifier
                .size(48.dp)
        )
    }
}

@Composable
fun SummaryCompletedDialog(onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .height(160.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "요약이 완료되었습니다",
                    style = AppTypography.heading01,
                    color = HelloWorldGrayScale500
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "상담요약은 마이페이지 > 내 상담 요약에서 볼 수 있습니다.",
                    style = AppTypography.label01,
                    color = HelloWorldGrayScale300
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(HelloWorldMain400, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "확인",
                    style = AppTypography.label01,
                    color = HelloWorldMain0
                )
            }
        }
    }
}





