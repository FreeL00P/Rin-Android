package com.rin.android.data.remote.api

import com.rin.android.data.remote.dto.*
import okhttp3.ResponseBody
import retrofit2.http.*

interface FeedApi {
    @GET("feed")
    suspend fun getFeeds(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("type") type: String? = null,
    ): FeedListResponseDto

    @GET("feed/{id}")
    suspend fun getFeed(@Path("id") id: String): FeedDto

    @POST("feed")
    suspend fun createFeed(@Body request: CreateFeedRequestDto): ResponseBody

    @POST("feed/{id}")
    suspend fun updateFeed(@Path("id") id: Int, @Body request: UpdateFeedRequestDto): ResponseBody

    @DELETE("feed/{id}")
    suspend fun deleteFeed(@Path("id") id: Int): ResponseBody

    @GET("feed/timeline")
    suspend fun getTimeline(): List<TimelineItemDto>

    @GET("search/{keyword}")
    suspend fun search(
        @Path("keyword") keyword: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
    ): FeedListResponseDto
}
