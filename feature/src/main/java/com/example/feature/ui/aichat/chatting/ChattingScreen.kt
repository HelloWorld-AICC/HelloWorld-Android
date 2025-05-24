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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.component.BackHeader
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale100
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldMain0
import com.example.core.ui.theme.HelloWorldMain100
import com.example.core.ui.theme.HelloWorldMain200
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
            items(messages.reversed()) { msg ->
                ChatBubble(msg)
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
fun ChatBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (msg.isUser) HelloWorldMain500 else HelloWorldMain100,
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 11.dp)
        ) {
            Text(
                msg.text,
                style = AppTypography.body01,
                color = if (msg.isUser) HelloWorldMain0 else HelloWorldMain700
            )
        }
    }
}
