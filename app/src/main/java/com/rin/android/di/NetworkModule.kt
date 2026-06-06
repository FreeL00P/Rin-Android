package com.rin.android.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.rin.android.data.remote.api.AuthApi
import com.rin.android.data.remote.api.FeedApi
import com.rin.android.data.remote.api.StorageApi
import com.rin.android.data.remote.api.UserApi
import com.rin.android.data.remote.interceptor.AuthInterceptor
import com.rin.android.data.remote.interceptor.BaseUrlInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        baseUrlInterceptor: BaseUrlInterceptor,
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(baseUrlInterceptor)
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit = Retrofit.Builder()
        .baseUrl("${BaseUrlInterceptor.PLACEHOLDER}/api/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides @Singleton
    fun provideFeedApi(retrofit: Retrofit): FeedApi = retrofit.create(FeedApi::class.java)

    @Provides @Singleton
    fun provideStorageApi(retrofit: Retrofit): StorageApi = retrofit.create(StorageApi::class.java)
}
