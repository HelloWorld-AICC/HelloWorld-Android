package com.example.core.util.extension

fun Long.toCategoryName(): String {
    return when(this) {
        0L -> "직장 내 고충"
        1L -> "체류 및 비자"
        2L -> "산재 및 의료"
        3L -> "기타"
        else -> "직장 내 고충"
    }
}

fun String.toCategoryId(): Long {
    return when(this) {
        "직장 내 고충" -> 0L
        "체류 및 비자" -> 1L
        "산재 및 의료" -> 2L
        "기타" -> 3L
        else -> 0L
    }
}