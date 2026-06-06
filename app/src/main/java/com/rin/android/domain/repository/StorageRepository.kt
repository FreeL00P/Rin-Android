package com.rin.android.domain.repository

interface StorageRepository {
    suspend fun uploadImage(fileName: String, byteArray: ByteArray): Result<String>
}
