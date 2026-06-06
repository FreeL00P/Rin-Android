package com.rin.android.domain.repository

import com.rin.android.data.remote.dto.*

interface UserRepository {
    suspend fun getProfile(): Result<UserDto>
    suspend fun updateProfile(request: UpdateProfileRequestDto): Result<SuccessResponseDto>
}
