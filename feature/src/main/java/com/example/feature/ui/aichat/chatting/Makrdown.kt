package com.example.feature.ui.aichat.chatting

// 파일 상단 import
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified,
    selectable: Boolean = true
) {
    val context = LocalContext.current
    val markwon = remember(context) {
        Markwon.builder(context)
            .build()
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            TextView(ctx).apply {
                if (selectable) setTextIsSelectable(true)
            }
        },
        update = { tv ->
            if (textColor != Color.Unspecified) {
                tv.setTextColor(android.graphics.Color.argb(
                    (textColor.alpha * 255).toInt(),
                    (textColor.red * 255).toInt(),
                    (textColor.green * 255).toInt(),
                    (textColor.blue * 255).toInt()
                ))
            }
            markwon.setMarkdown(tv, markdown)
        }
    )
}
