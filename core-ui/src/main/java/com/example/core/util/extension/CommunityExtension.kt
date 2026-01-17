package com.example.core.util.extension

import com.example.core.ui.R

fun Long.toCategoryName(): Int {
    return when(this) {
        0L -> R.string.community_category_workplace
        1L -> R.string.community_category_visa
        2L -> R.string.community_category_insurance
        3L -> R.string.community_category_etc
        else -> R.string.community_category_workplace
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