package com.rin.android.data.repository

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.rin.android.data.local.UserPreferences
import com.rin.android.data.remote.api.AuthApi
import com.rin.android.data.remote.dto.*
import com.rin.android.domain.repository.AuthRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences,
    private val json: Json,
    private val okHttpClient: OkHttpClient,
) : AuthRepository {

    private suspend fun buildAuthApi(baseUrl: String): AuthApi {
        val url = baseUrl.trimEnd('/') + "/api/"
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AuthApi::class.java)
    }

    override suspend fun getAuthStatus(baseUrl: String): Result<AuthStatusDto> = runCatching {
        buildAuthApi(baseUrl).getAuthStatus()
    }

    override suspend fun login(baseUrl: String, username: String, password: String): Result<LoginResponseDto> = runCatching {
        val api = buildAuthApi(baseUrl)
        val response = api.login(LoginRequestDto(username, password))
        if (response.success && response.token != null) {
            userPreferences.saveToken(response.token)
            userPreferences.saveBaseUrl(baseUrl)
        }
        response
    }

    override suspend fun logout(): Result<SuccessResponseDto> = runCatching {
        userPreferences.clearAll()
        SuccessResponseDto(success = true)
    }

    override fun isLoggedIn(): Boolean = userPreferences.isLoggedInSync()
    override fun hasBaseUrl(): Boolean = userPreferences.hasBaseUrlSync()
}
