package com.example.core.util.extension

fun String.truncateWithEllipsis(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.take(maxLength) + "..."
    } else {
        this
    }
}