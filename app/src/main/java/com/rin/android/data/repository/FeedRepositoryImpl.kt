package com.rin.android.data.repository

import com.rin.android.data.remote.api.FeedApi
import com.rin.android.data.remote.dto.*
import com.rin.android.domain.repository.FeedRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepositoryImpl @Inject constructor(
    private val feedApi: FeedApi,
) : FeedRepository {
    override suspend fun getFeeds(page: Int, limit: Int, type: String?): Result<FeedListResponseDto> =
        runCatching { feedApi.getFeeds(page, limit, type) }

    override suspend fun getFeed(id: String): Result<FeedDto> =
        runCatching { feedApi.getFeed(id) }

    override suspend fun createFeed(request: CreateFeedRequestDto): Result<FeedCreateResponseDto> =
        runCatching {
            feedApi.createFeed(request).string()
            FeedCreateResponseDto()
        }

    override suspend fun updateFeed(id: Int, request: UpdateFeedRequestDto): Result<String> =
        runCatching { feedApi.updateFeed(id, request).string() }

    override suspend fun deleteFeed(id: Int): Result<String> =
        runCatching { feedApi.deleteFeed(id).string() }

    override suspend fun search(keyword: String, page: Int?, limit: Int?): Result<FeedListResponseDto> =
        runCatching { feedApi.search(keyword, page, limit) }
}
