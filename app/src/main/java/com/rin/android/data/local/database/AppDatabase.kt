package com.rin.android.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rin.android.data.local.dao.DraftDao
import com.rin.android.data.local.entity.DraftEntity

const val DRAFT_DATABASE_NAME = "rin_drafts.db"

@Database(entities = [DraftEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun draftDao(): DraftDao
}
