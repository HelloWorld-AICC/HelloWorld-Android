package com.example.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LanguageRequest(
    @SerialName("language")
    val language: Long,
)

@Serializable
enum class Language(val code: Long, val displayName: String, val national: National, val localeCode: String) {
    ENGLISH(1, "English", National.ENGLISH, "en"),
    KOREAN(2, "Korean", National.KOREAN, "ko"),
    JAPANESE(3, "Japanese", National.JAPANESE, "ja"),
    CHINESE(4, "Chinese", National.CHINESE, "zh-CN"),
    VIETNAMESE(5, "Vietnamese", National.VIETNAMESE, "vi");

    companion object {
        fun fromCode(code: Long): Language? {
            return entries.find { it.code == code }
        }
    }
}

enum class National {
    ENGLISH,
    KOREAN,
    JAPANESE,
    CHINESE,
    VIETNAMESE,
}