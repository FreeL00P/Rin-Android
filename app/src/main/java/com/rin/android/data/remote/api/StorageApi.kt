package com.rin.android.data.remote.api

import com.rin.android.data.remote.dto.UploadResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface StorageApi {
    @Multipart
    @POST("storage")
    suspend fun uploadFile(
        @Part("key") key: RequestBody,
        @Part file: MultipartBody.Part,
    ): UploadResponseDto
}
