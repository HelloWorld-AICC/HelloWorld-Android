package com.example.core.data.di

import com.example.core.data.mypage.MyPageRepository
import com.example.core.data.mypage.MyPageRepositoryImpl
import com.example.core.data.token.TokenRepositoryImpl
import com.example.network.interceptor.TokenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindsTokenRepository(
        tokenRepositoryImpl: TokenRepositoryImpl
    ): TokenRepository

    @Binds
    abstract fun bindsMyPageRepository(
        myPageRepositoryImpl: MyPageRepositoryImpl
    ): MyPageRepository
}