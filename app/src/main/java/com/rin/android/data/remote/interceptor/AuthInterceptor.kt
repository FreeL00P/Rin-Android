package com.rin.android.data.remote.interceptor

import com.rin.android.data.local.UserPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val userPreferences: UserPreferences,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { userPreferences.getToken() }

        val request = if (!token.isNullOrBlank() && originalRequest.header("Authorization") == null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}
