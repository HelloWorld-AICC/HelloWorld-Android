package com.example.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldMain0
import com.example.core.ui.theme.HelloWorldMain400
import kotlinx.coroutines.delay

@Composable
fun HWToast(
    data: ToastData,
) {

    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    LaunchedEffect(data) {
        delay(data.duration)
        visibleState.targetState = false
        delay(300)
        data.onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(bottom = 138.dp)
            .zIndex(1f),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visibleState = visibleState,
            enter = fadeIn(tween(300)) + slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(300)
            ),
            exit = fadeOut(tween(300)) + slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(300)
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HelloWorldMain400, RoundedCornerShape(8.dp))
                    .padding(horizontal = 24.dp, vertical = 9.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = data.text,
                    style = AppTypography.label01,
                    color = HelloWorldMain0
                )
            }
        }
    }
}

data class ToastData(
    val text: String = "",
    val duration: Long = 2000,
    val onDismiss: () -> Unit = {},
)