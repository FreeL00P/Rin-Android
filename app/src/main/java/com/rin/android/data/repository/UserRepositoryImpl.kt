package com.rin.android.data.repository

import com.rin.android.data.remote.api.UserApi
import com.rin.android.data.remote.dto.*
import com.rin.android.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
) : UserRepository {
    override suspend fun getProfile(): Result<UserDto> = runCatching { userApi.getProfile() }
    override suspend fun updateProfile(request: UpdateProfileRequestDto): Result<SuccessResponseDto> =
        runCatching { userApi.updateProfile(request) }
}
