package com.example.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// 라이트 테마
private val LightColorScheme = lightColorScheme(
    background = HelloWorldMain0,
    error = HelloWorldError,
)

// 다크 테마 
private val DarkColorScheme = darkColorScheme(
)

@Composable
fun HelloWorldTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
