// feature/home/HomeScreen.kt
package com.example.feature.ui.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*

@Composable
fun HomeScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("이곳은 홈 화면입니다!")
    }
}
