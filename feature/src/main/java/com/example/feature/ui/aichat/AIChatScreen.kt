package com.example.feature.ui.aichat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.data.model.ChattingRoom
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale100
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldMain200
import com.example.core.ui.theme.HelloWorldMain500
import com.example.feature.R

@Composable
internal fun AiChatScreen(
    onPostClick: (String?) -> Unit,
    viewModel: AIChatViewModel = hiltViewModel()
) {
    val chattingRooms by viewModel.chattingRooms.collectAsState()

    // 화면 진입 시 채팅방 목록 불러오기
    LaunchedEffect(Unit) {
        viewModel.getAIChattingRooms()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HeaderTitle()
        HorizontalDivider(color = HelloWorldMain200)
        Spacer(modifier = Modifier.height(24.dp))
        Banner()
        Spacer(modifier = Modifier.height(24.dp))
        RecentChatSection (
            onPostClick = onPostClick
        )
        Spacer(modifier = Modifier.height(8.dp))

        // 채팅방이 있을 때만 리스트 출력
        if (!chattingRooms.isNullOrEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
            ) {
                items(chattingRooms) { room ->
                    ConversationItem(
                        chattingRoom = room,
                        onPostClick = { onPostClick(room.roomId) }
                    )
                }
            }
        }
    }
}

@Composable
fun Banner() {
    Image(
        painter = painterResource(id = R.drawable.ic_banner),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun ChatNewButton(
    modifier: Modifier = Modifier,
    onPostClick: () -> Unit
) {
    OutlinedButton(
        onClick = onPostClick,
        modifier = modifier
            .defaultMinSize(minWidth = 0.dp, minHeight = 0.dp)
            .height(30.dp),
        border = BorderStroke(1.dp, HelloWorldGrayScale100),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(
            start = 14.dp, end = 14.dp, top = 4.dp, bottom = 6.dp
        ),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
    ) {
        Text(
            text = "✚  새 채팅",
            style = AppTypography.label01,
            color = HelloWorldMain500
        )
    }
}

@Composable
fun RecentChatSection(
    onPostClick: (String?) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "최근 나의 상담",
                style = AppTypography.heading04,
                color = Color.Black
            )
            ChatNewButton { onPostClick("new") }
        }
    }
}

@Composable
fun ConversationItem(
    chattingRoom: ChattingRoom,
    onPostClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp)
            .clickable(onClick = onPostClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_chat_item),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "최근 상담", // TODO: 추후 날짜 필드 추가 시 교체
                style = AppTypography.label01,
                color = HelloWorldGrayScale300
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = chattingRoom.title,
            style = AppTypography.heading04,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = HelloWorldMain200)
    }
}

@Composable
fun HeaderTitle(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ai Chat",
            style = AppTypography.heading04,
            textAlign = TextAlign.Center
        )
    }
}
