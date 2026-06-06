package com.rin.android.data.remote.api

import com.rin.android.data.remote.dto.*
import retrofit2.http.*

interface UserApi {
    @GET("user/profile")
    suspend fun getProfile(): UserDto

    @PUT("user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequestDto): SuccessResponseDto

    @POST("user/logout")
    suspend fun logout(): SuccessResponseDto
}
