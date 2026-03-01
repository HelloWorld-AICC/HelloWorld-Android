package com.example.feature.ui.aichat.chatting

import MarkdownText
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.data.model.aichat.AIChatMessage
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
import com.example.core.util.extension.advancedImePadding
import com.example.feature.R
import com.example.core.ui.R as languageR
import kotlinx.coroutines.launch

@Composable
internal fun RecentChattingScreen(
    roomId: String,
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val greeting = stringResource(languageR.string.ai_greeting)
    val prompt = stringResource(languageR.string.ai_prompt)

    LaunchedEffect(roomId, greeting, prompt) {
        viewModel.loadChatLog(roomId, greeting, prompt)
    }

    val selectedChatId by viewModel.selectedChatId.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()

    var userInput by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    fun scrollToBottom() = scope.launch {
        // reverseLayout=true 이므로 index 0 이 최하단
        listState.animateScrollToItem(0)
    }

    val messages = selectedChatId?.let { chatMessages[it].orEmpty() } ?: emptyList()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var showSummaryDialog by remember { mutableStateOf(false) }

    val lastSummarizableBot = remember(messages) {
        val botMessages = messages.filter { it.sender.equals("bot", ignoreCase = true) }

        botMessages.drop(2).lastOrNull()
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) scrollToBottom()
    }

    LaunchedEffect(isTyping) {
        if (isTyping) scrollToBottom()
    }

    LaunchedEffect(Unit) {
        viewModel.summaryCompleted.collect {
            showSummaryDialog = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .advancedImePadding()
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            }
    ) {
        BackHeader(title = stringResource(languageR.string.home_chatbot_title), onBackClick = onBackClick)
        HorizontalDivider(color = HelloWorldMain200)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HelloWorldMain100)
                .padding(horizontal = 26.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(languageR.string.chat_warn),
                style = AppTypography.label02,
                color = HelloWorldGrayScale300
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            reverseLayout = true,
            state = listState,
            verticalArrangement = Arrangement.Top
        ) {
            if (isTyping) {
                item { TypingBubble() }
            }
            items(messages.reversed()) { msg ->
                ChatBubble(
                    msg = msg,
                    viewModel = viewModel,
                    showSummaryIcon = (msg === lastSummarizableBot) // ✅ 인트로는 자연히 제외
                )
            }
        }

        Row(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 10.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, HelloWorldGrayScale100, RoundedCornerShape(8.dp))
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    textStyle = AppTypography.label02,
                    maxLines = 6,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
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
                        .padding(start = 12.dp, end = 44.dp)
                )

                if (userInput.isEmpty()) {
                    Text(
                        text = stringResource(languageR.string.ai_input_placeholder),
                        style = AppTypography.label02,
                        color = HelloWorldGrayScale300,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_up),
                    contentDescription = "Send",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clickable(
                            enabled = userInput.isNotBlank(),
                            onClick = {
                                if (userInput.isNotBlank() && selectedChatId != null) {
                                    viewModel.sendUserMessage(selectedChatId!!, userInput)
                                    userInput = ""
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    scrollToBottom()
                                }
                            }
                        ),
                    colorFilter = if (userInput.isNotBlank()) {
                        ColorFilter.tint(HelloWorldMain500)
                    } else {
                        ColorFilter.tint(HelloWorldGrayScale100)
                    }
                )
            }
        }
    }
    if (showSummaryDialog) {
        SummaryCompletedDialog(onDismiss = { showSummaryDialog = false })
    }
}

@Composable
fun ChatBubble(msg: AIChatMessage, viewModel: ChatViewModel, showSummaryIcon: Boolean
) {
    val isUser = msg.sender == "user"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .background(
                        if (isUser) HelloWorldMain500 else HelloWorldMain100,
                        RoundedCornerShape(8.dp)
                    )
                    .widthIn(max = 290.dp)
                    .padding(horizontal = 16.dp, vertical = 11.dp)
            ) {
                if (isUser) {
                    // 유저 메시지는 평문
                    Text(
                        text = msg.content,
                        style = AppTypography.body01,
                        color = HelloWorldMain0
                    )
                } else {
                    // 봇 메시지는 마크다운 렌더
                    MarkdownText(
                        markdown = msg.content,
                        textColor = HelloWorldMain700,   // 버블 색에 맞춰 글자색
                    )
                }
            }

            // 마지막 봇 메시지에만 아이콘 노출
            if (!isUser && msg.sender.equals("bot", true) && showSummaryIcon) {
                Image(
                    painter = painterResource(id = R.drawable.ic_summary),
                    contentDescription = "Summarize",
                    modifier = Modifier
                        .size(20.dp)
                        .offset(x = 23.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { viewModel.summarizeMessage() }
                )
            }
        }
    }
}


@Composable
fun TypingBubble() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
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
                    text = stringResource(languageR.string.ai_summary_complete_title),
                    style = AppTypography.heading01,
                    color = HelloWorldGrayScale500
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(languageR.string.ai_summary_location),
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
                    text = stringResource(languageR.string.confirm),
                    style = AppTypography.label01,
                    color = HelloWorldMain0
                )
            }
        }
    }
}

