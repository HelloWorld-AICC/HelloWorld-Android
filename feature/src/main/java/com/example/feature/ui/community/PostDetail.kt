package com.example.feature.ui.community

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale800

@Composable
internal fun CommunityPostDetail(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "커뮤니티",
                style = AppTypography.body01,
                color = HelloWorldGrayScale800
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityPostDetailPreview() {
    CommunityPostDetail()
}