package com.example.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.feature.R

@Composable
fun SplashLogo() {
    Image(
        painter = painterResource(id = R.drawable.splash_logo),
        contentDescription = "Logo",
        modifier = Modifier.size(120.dp)
    )
}
