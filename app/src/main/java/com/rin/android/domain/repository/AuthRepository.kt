package com.rin.android.domain.repository

import com.rin.android.data.remote.dto.*

interface AuthRepository {
    suspend fun getAuthStatus(baseUrl: String): Result<AuthStatusDto>
    suspend fun login(baseUrl: String, username: String, password: String): Result<LoginResponseDto>
    suspend fun logout(): Result<SuccessResponseDto>
    fun isLoggedIn(): Boolean
    fun hasBaseUrl(): Boolean
}
