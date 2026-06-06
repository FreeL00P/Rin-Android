package com.rin.android.data.remote.api

import com.rin.android.data.remote.dto.*
import retrofit2.http.*

interface AuthApi {
    @GET("auth/status")
    suspend fun getAuthStatus(): AuthStatusDto

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto
}
