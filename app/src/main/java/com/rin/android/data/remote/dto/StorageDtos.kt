package com.rin.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExternalUploadResponseDto(
    val data: String = "",
)

@Serializable
data class UploadResponseDto(
    val url: String = "",
)

@Serializable
data class TagDto(
    val id: Int,
    val name: String,
    val count: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = "",
)
