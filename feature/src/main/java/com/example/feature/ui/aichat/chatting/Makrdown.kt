import android.text.method.MovementMethod
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import org.commonmark.node.Link

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified,
) {
    val context = LocalContext.current

    val markwon = remember(context) {
        Markwon.builder(context)
            .usePlugin(SoftBreakAddsNewLinePlugin.create())
            // 🔒 링크 노드를 “자식 텍스트만” 그리게 해서 클릭 스팬 자체를 만들지 않음
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    builder.on(Link::class.java) { visitor, link ->
                        // 링크를 무시하고 내부 텍스트만 그대로 출력
                        visitor.visitChildren(link)
                    }
                }
            })
            .build()
    }

    val fixed = remember(markdown) {
        // 숫자 목록 줄바꿈 보정
        markdown.replace(Regex("(?m)\\n\\n(?=\\d+\\.\\s)"), "\n\u200B\n")
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            TextView(ctx).apply {
                // ✅ 선택/클릭/롱클릭/오토링크 전부 차단
                setTextIsSelectable(false)
                isClickable = false
                isLongClickable = false
                linksClickable = false
                autoLinkMask = 0
                highlightColor = 0
                movementMethod = null as MovementMethod?

                customSelectionActionModeCallback = object : ActionMode.Callback {
                    override fun onCreateActionMode(mode: ActionMode, menu: Menu) = false
                    override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false
                    override fun onActionItemClicked(mode: ActionMode, item: MenuItem) = false
                    override fun onDestroyActionMode(mode: ActionMode) {}
                }
                setOnLongClickListener { true }
            }
        },
        update = { tv ->
            if (textColor != Color.Unspecified) {
                tv.setTextColor(
                    android.graphics.Color.argb(
                        (textColor.alpha * 255).toInt(),
                        (textColor.red * 255).toInt(),
                        (textColor.green * 255).toInt(),
                        (textColor.blue * 255).toInt()
                    )
                )
            }
            markwon.setMarkdown(tv, fixed)
        }
    )
}
