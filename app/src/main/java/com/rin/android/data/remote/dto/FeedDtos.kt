package com.rin.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedListResponseDto(
    val size: Int = 0,
    val data: List<FeedListItemDto> = emptyList(),
    val hasNext: Boolean = false,
)

@Serializable
data class FeedListItemDto(
    val id: Int,
    val title: String? = null,
    val summary: String = "",
    val avatar: String? = null,
    val hashtags: List<HashtagDto> = emptyList(),
    val user: FeedUserDto? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
    val pv: Int = 0,
    val uv: Int = 0,
    val draft: Int? = null,
    val listed: Int? = null,
)

@Serializable
data class FeedDto(
    val id: Int,
    val alias: String? = null,
    val title: String? = null,
    val summary: String = "",
    val content: String = "",
    val aiSummary: String = "",
    @SerialName("ai_summary_status") val aiSummaryStatus: String = "idle",
    val hashtags: List<HashtagDto> = emptyList(),
    val user: FeedUserDto? = null,
    val listed: Int = 1,
    val draft: Int = 0,
    val top: Int? = null,
    val uid: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = "",
    val pv: Int = 0,
    val uv: Int = 0,
)

@Serializable
data class FeedUserDto(
    val id: Int,
    val username: String,
    val avatar: String? = null,
)

@Serializable
data class HashtagDto(
    val id: Int,
    val name: String,
)

@Serializable
data class CreateFeedRequestDto(
    val title: String,
    val content: String,
    val listed: Boolean,
    val draft: Boolean,
    val tags: List<String>,
)

@Serializable
data class UpdateFeedRequestDto(
    val title: String? = null,
    val content: String? = null,
    val summary: String? = null,
    val alias: String? = null,
    val listed: Boolean,
    val draft: Boolean? = null,
    val tags: List<String>? = null,
    val top: Int? = null,
    val createdAt: String? = null,
)

@Serializable
data class FeedCreateResponseDto(
    val insertedId: Int = 0,
)

@Serializable
data class TimelineItemDto(
    val id: Int,
    val title: String? = null,
    val createdAt: String = "",
)

@Serializable
data class AdjacentFeedResponseDto(
    val previousFeed: AdjacentFeedDto? = null,
    val nextFeed: AdjacentFeedDto? = null,
)

@Serializable
data class AdjacentFeedDto(
    val id: Int,
    val title: String? = null,
    val summary: String = "",
    val hashtags: List<HashtagDto> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = "",
)
