package com.example.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale200
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale500
import com.example.core.ui.theme.HelloWorldMain0
import com.example.core.ui.theme.HelloWorldMain400

@Composable
fun HWDialog(
    data: DialogData,
) {
    Dialog(
        onDismissRequest = { data.onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = data.title,
                    style = AppTypography.heading01,
                    color = HelloWorldGrayScale500,
                )
                Text(
                    text = data.subTitle,
                    style = AppTypography.label01,
                    color = HelloWorldGrayScale300,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(HelloWorldGrayScale200)
                        .clickable { data.onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = data.dismiss,
                        style = AppTypography.label01,
                        color = HelloWorldMain0,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(HelloWorldMain400)
                        .clickable { data.onConfirm() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = data.confirm,
                        style = AppTypography.label01,
                        color = HelloWorldMain0,
                    )
                }
            }
        }
    }
}

data class DialogData(
    val title: String = "",
    val subTitle: String = "",
    val dismiss: String = "취소",
    val confirm: String = "확인",
    val onDismiss: () -> Unit,
    val onConfirm: () -> Unit = {},
)