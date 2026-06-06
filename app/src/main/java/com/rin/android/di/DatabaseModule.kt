package com.rin.android.di

import android.content.Context
import androidx.room.Room
import com.rin.android.data.local.database.AppDatabase
import com.rin.android.data.local.database.DRAFT_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DRAFT_DATABASE_NAME)
            .build()

    @Provides
    fun provideDraftDao(database: AppDatabase) = database.draftDao()
}
