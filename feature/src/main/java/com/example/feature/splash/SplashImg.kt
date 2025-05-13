package com.example.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.feature.R

@Composable
fun SplashImg() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // 지구
        Image(
            painter = painterResource(id = R.drawable.splash_img),
            contentDescription = "splash_img",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(1024.dp)
        )
    }
}
