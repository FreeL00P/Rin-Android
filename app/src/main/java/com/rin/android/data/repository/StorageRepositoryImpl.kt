package com.rin.android.data.repository

import android.util.Base64
import com.rin.android.data.local.UserPreferences
import com.rin.android.data.remote.api.StorageApi
import com.rin.android.domain.repository.StorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences,
    private val storageApi: StorageApi,
) : StorageRepository {
    private val cleanClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    override suspend fun uploadImage(fileName: String, byteArray: ByteArray): Result<String> {
        val mode = userPreferences.getUploadModeSync()
        return if (mode == "external") {
            uploadToExternal(fileName, byteArray)
        } else {
            uploadToRin(fileName, byteArray)
        }
    }

    private suspend fun uploadToRin(fileName: String, byteArray: ByteArray): Result<String> = runCatching {
        val requestBody = byteArray.toRequestBody("image/*".toMediaType())
        val filePart = MultipartBody.Part.createFormData("file", fileName, requestBody)
        val keyBody = fileName.toRequestBody(MultipartBody.FORM)
        val response = storageApi.uploadFile(keyBody, filePart)
        response.url
    }

    private suspend fun uploadToExternal(fileName: String, byteArray: ByteArray): Result<String> = runCatching {
        val configuredUrl = userPreferences.getUploadUrl()?.trim()
            ?: return Result.failure(IllegalStateException("Upload URL not configured"))
        if (configuredUrl.isBlank()) return Result.failure(IllegalStateException("Upload URL not configured"))
        val uploadUrl = configuredUrl.trimEnd('/').let { url ->
            if (url.endsWith("/upload")) url else "$url/upload"
        }

        val user = userPreferences.getUploadUser()?.trim() ?: ""
        val pass = userPreferences.getUploadPass() ?: ""

        val requestBody = byteArray.toRequestBody("image/*".toMediaType())
        val filePart = MultipartBody.Part.createFormData("file", fileName, requestBody)
        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addPart(filePart)
            .build()

        val credentials = Base64.encodeToString("$user:$pass".toByteArray(), Base64.NO_WRAP)

        val request = Request.Builder()
            .url(uploadUrl)
            .addHeader("Authorization", "Basic $credentials")
            .addHeader("Accept", "application/json")
            .post(multipartBody)
            .build()

        withContext(Dispatchers.IO) {
            val response = cleanClient.newCall(request).execute()
            val rawBody = response.body?.string() ?: throw Exception("Empty response")
            if (!response.isSuccessful) throw Exception("Upload failed: ${response.code} body=$rawBody")

            val body = rawBody.trim().removePrefix("\uFEFF")
            if (!body.startsWith("{")) throw Exception("Upload endpoint returned non-JSON response. Check URL: $uploadUrl")
            val json = Json { ignoreUnknownKeys = true }
            val parsed = json.decodeFromString<ExternalUploadResponse>(body)
            if (parsed.data.isBlank()) throw Exception("Upload response missing data: $rawBody")
            parsed.data
        }
    }
}

@kotlinx.serialization.Serializable
private data class ExternalUploadResponse(val data: String = "")
