package com.rin.android.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "rin_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    suspend fun getToken(): String? = prefs.getString("auth_token", null)
    suspend fun saveToken(token: String) { prefs.edit().putString("auth_token", token).apply() }
    suspend fun clearToken() { prefs.edit().remove("auth_token").apply() }
    suspend fun getBaseUrl(): String? = prefs.getString("base_url", null)
    suspend fun saveBaseUrl(url: String) { prefs.edit().putString("base_url", url).apply() }
    suspend fun clearAll() { prefs.edit().clear().apply() }

    fun isLoggedInSync(): Boolean = !prefs.getString("auth_token", null).isNullOrBlank()
    fun hasBaseUrlSync(): Boolean = !prefs.getString("base_url", null).isNullOrBlank()

    suspend fun getUploadUrl(): String? = prefs.getString("upload_url", null)
    suspend fun saveUploadUrl(url: String) { prefs.edit().putString("upload_url", url).apply() }
    suspend fun getUploadUser(): String? = prefs.getString("upload_user", null)
    suspend fun saveUploadUser(user: String) { prefs.edit().putString("upload_user", user).apply() }
    suspend fun getUploadPass(): String? = prefs.getString("upload_pass", null)
    suspend fun saveUploadPass(pass: String) { prefs.edit().putString("upload_pass", pass).apply() }

    fun getUploadModeSync(): String = prefs.getString("upload_mode", "rin") ?: "rin"
    suspend fun saveUploadMode(mode: String) { prefs.edit().putString("upload_mode", mode).apply() }
}
