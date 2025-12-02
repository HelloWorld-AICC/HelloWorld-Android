package com.example.feature.ui.aichat.chatting

// 파일 상단 import
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.linkify.LinkifyPlugin

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified,
    selectable: Boolean = false // <- 링크 우선이면 false 권장
) {
    val context = LocalContext.current

    val markwon = remember(context) {
        Markwon.builder(context)
            .usePlugin(SoftBreakAddsNewLinePlugin.create())
            .usePlugin(LinkifyPlugin.create()) // URL/markdown 링크 인식
            .build()
    }

    val fixed = remember(markdown) {
        markdown.replace(Regex("(?m)\\n\\n(?=\\d+\\.\\s)"), "\n\u200B\n")
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            TextView(ctx).apply {
                // ✅ 링크 클릭 가능하도록
                linksClickable = true
                movementMethod = LinkMovementMethod.getInstance()

                // ⚠️ 선택과 링크 클릭은 충돌 가능
                //    꼭 필요할 때만 true로; 그러면 일부 기기에서 링크 클릭이 먹지 않을 수 있음
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
            markwon.setMarkdown(tv, fixed)
        }
    )
}


