package com.example.core.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.core.ui.R

// Pretendard Font 설정
val Pretendard = FontFamily(
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
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 72.sp,
        letterSpacing = (-0.03).em
    ),
    title01 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        letterSpacing = (-0.03).em
    ),
    title02 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        letterSpacing = (-0.03).em
    ),
    title03 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        letterSpacing = (-0.03).em
    ),
    heading01 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = (-0.03).em
    ),
    heading02 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        letterSpacing = (-0.01).em
    ),
    heading03 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        letterSpacing = (-0.01).em
    ),
    heading04 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        letterSpacing = (-0.01).em
    ),
    body01 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = (-0.02).em
    ),
    body02 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = (-0.02).em
    ),
    label01 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = (-0.01).em
    ),
    label02 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = (-0.01).em
    ),
    label03 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 0.em
    ),
    carousel = TextStyle (
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 6.sp,
        letterSpacing = (-0.01).em
    )
)

// 커스텀 스타일은 따로 정의
val HeadlineSmall2 = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    letterSpacing = (-0.01).em
)
