package com.example.core.util.extension

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.truncateWithEllipsis(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.take(maxLength) + "..."
    } else {
        this
    }
}

fun String.toFormattedDate(): String {
    return try {
        val localDateTime = LocalDateTime.parse(this)
        val formatter = DateTimeFormatter.ofPattern("yy.MM.dd")
        localDateTime.format(formatter)
    } catch (e: Exception) {
        this
    }
}