package com.example.feature.ui.aichat.chatting

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.component.BackHeader
import com.example.core.ui.theme.HelloWorldMain0
import com.example.core.ui.theme.HelloWorldMain100
import com.example.core.ui.theme.HelloWorldMain500
import com.example.core.ui.theme.HelloWorldMain700

@Composable
internal fun RecentChattingScreen (
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    var userInput by remember { mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxSize()
    ) {
        BackHeader(
            title = "AI Chat",
            onBackClick = { onBackClick() }
        )

        LazyColumn(modifier = Modifier.weight(1f), reverseLayout = true) {
            items(messages.reversed()) { msg ->
                ChatBubble(msg)
            }
        }

        Row(modifier = Modifier.padding(8.dp)) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("메시지를 입력하세요") },
                keyboardActions = KeyboardActions(onSend = {
                    viewModel.sendUserMessage(userInput)
                    userInput = ""
                }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send)
            )
            IconButton(onClick = {
                viewModel.sendUserMessage(userInput)
                userInput = ""
            }) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = "Send"
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
            .padding(4.dp),
        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (msg.isUser) HelloWorldMain500 else HelloWorldMain100,
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                msg.text,
                color = if (msg.isUser) HelloWorldMain0 else HelloWorldMain700
            )
        }
    }
}
