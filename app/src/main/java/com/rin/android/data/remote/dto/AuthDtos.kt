package com.rin.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthStatusDto(
    val github: Boolean = false,
    val password: Boolean = false,
)

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String,
)

@Serializable
data class LoginResponseDto(
    val success: Boolean = false,
    val token: String? = null,
    val user: UserDto? = null,
)

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val avatar: String? = null,
    val permission: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class SuccessResponseDto(
    val success: Boolean = false,
)

@Serializable
data class UpdateProfileRequestDto(
    val username: String? = null,
    val avatar: String? = null,
)
