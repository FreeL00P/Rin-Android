package com.rin.android.data.remote.interceptor

import com.rin.android.data.local.UserPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseUrlInterceptor @Inject constructor(
    private val userPreferences: UserPreferences,
) : Interceptor {

    companion object {
        const val PLACEHOLDER = "https://127.0.0.1"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (originalRequest.url.host != "127.0.0.1") {
            return chain.proceed(originalRequest)
        }

        val baseUrl = runBlocking { userPreferences.getBaseUrl() }?.trimEnd('/')
        if (baseUrl.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val base = baseUrl.toHttpUrl()
        val newUrl = originalRequest.url.newBuilder()
            .scheme(base.scheme)
            .host(base.host)
            .port(base.port)
            .build()

        return chain.proceed(originalRequest.newBuilder().url(newUrl).build())
    }
}
