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

data class CustomTypography(
    val display01: TextStyle,
    val title01: TextStyle,
    val title02: TextStyle,
    val title03: TextStyle,
    val heading01: TextStyle,
    val heading02: TextStyle,
    val heading03: TextStyle,
    val heading04: TextStyle,
    val body01: TextStyle,
    val body02: TextStyle,
    val label01: TextStyle,
    val label02: TextStyle,
    val label03: TextStyle,
    val carousel : TextStyle
)

val AppTypography = CustomTypography(
    display01 = TextStyle(
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 72.sp
    ),
    title01 = TextStyle(
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp
    ),
    title02 = TextStyle(
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp
    ),
    title03 = TextStyle(
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    heading01 = TextStyle(
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    heading02 = TextStyle(
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    heading03 = TextStyle(
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    heading04 = TextStyle(
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    body01 = TextStyle(
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    body02 = TextStyle(
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    label01 = TextStyle(
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ),
    label02 = TextStyle(
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    label03 = TextStyle(
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp
    ),
    carousel = TextStyle (
        fontFamily = PretendardVariable,
        fontWeight = FontWeight.Normal,
        fontSize = 6.sp
    )
)



