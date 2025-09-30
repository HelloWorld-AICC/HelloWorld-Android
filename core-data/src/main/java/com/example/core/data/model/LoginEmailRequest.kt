package com.example.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginEmailRequest(
    val email: String
)
