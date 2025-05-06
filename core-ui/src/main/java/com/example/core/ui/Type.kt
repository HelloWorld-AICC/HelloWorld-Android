package com.example.core.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.core.ui.R

val PretendardVariable = FontFamily(
    Font(R.font.pretendard_variable, weight = FontWeight.Normal) // Variable font 단일 파일
)

val AppTypography = Typography(
    displayLarge = TextStyle( // Display01
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 72.sp
    ),
    headlineLarge = TextStyle( // Title01
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp
    ),
    headlineMedium = TextStyle( // Title02
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle( // Title03
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle( // Heading01
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle( // Heading02
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle( // Heading03
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    bodyLarge = TextStyle( // Heading04
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle( // Body01
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle( // Body02
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle( // Label01
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ),
    labelMedium = TextStyle( // Label02
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)


