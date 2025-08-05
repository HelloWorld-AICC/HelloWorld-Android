package com.example.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LanguageRequest(
    @SerialName("language")
    val language: Long,
)

enum class Language(val code: Long, val displayName: String, val flag: String) {
    ENGLISH(1, "English", Flag.ENGLISH),
    KOREAN(2, "Korean", Flag.KOREAN),
    JAPANESE(3, "Japanese", Flag.JAPANESE),
    CHINESE(4, "Chinese", Flag.CHINESE),
    VIETNAMESE(5, "Vietnamese", Flag.VIETNAMESE);

    companion object {
        fun fromCode(code: Long): Language? {
            return entries.find { it.code == code }
        }
    }
}

object Flag {
    val ENGLISH = "\uD83C\uDDFA\uD83C\uDDF8"
    val KOREAN = "\uD83C\uDDF0\uD83C\uDDF7"
    val JAPANESE = "\uD83C\uDDEF\uD83C\uDDF5"
    val CHINESE = "\uD83C\uDDE8\uD83C\uDDF3"
    val VIETNAMESE = "\uD83C\uDDFB\uD83C\uDDF3"
}