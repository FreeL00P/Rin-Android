package com.rin.android.domain.repository

import com.rin.android.data.remote.dto.*

interface FeedRepository {
    suspend fun getFeeds(page: Int = 1, limit: Int = 20, type: String? = null): Result<FeedListResponseDto>
    suspend fun getFeed(id: String): Result<FeedDto>
    suspend fun createFeed(request: CreateFeedRequestDto): Result<FeedCreateResponseDto>
    suspend fun updateFeed(id: Int, request: UpdateFeedRequestDto): Result<String>
    suspend fun deleteFeed(id: Int): Result<String>
    suspend fun search(keyword: String, page: Int? = null, limit: Int? = null): Result<FeedListResponseDto>
}
