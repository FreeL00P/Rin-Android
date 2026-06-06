package com.rin.android.di

import com.rin.android.data.repository.*
import com.rin.android.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    @Binds @Singleton abstract fun bindFeedRepository(impl: FeedRepositoryImpl): FeedRepository
    @Binds @Singleton abstract fun bindDraftRepository(impl: DraftRepositoryImpl): DraftRepository
    @Binds @Singleton abstract fun bindStorageRepository(impl: StorageRepositoryImpl): StorageRepository
    @Binds @Singleton abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
