package com.rin.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drafts")
data class DraftEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val summary: String = "",
    val tags: String = "",
    val alias: String = "",
    val listed: Boolean = true,
    val draft: Boolean = true,
    val remoteId: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String = "",
)
