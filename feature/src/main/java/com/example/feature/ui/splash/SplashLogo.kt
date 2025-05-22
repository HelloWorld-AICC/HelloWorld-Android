package com.example.feature.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.feature.R

@Composable
fun SplashLogo() {
    Image(
        painter = painterResource(id = R.drawable.splash_logo),
        contentDescription = "Logo",
    )
}
