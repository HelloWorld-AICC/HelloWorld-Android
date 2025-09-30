package com.example.core.data.di

import com.example.core.data.common.LanguageRepository
import com.example.core.data.common.LanguageRepositoryImpl
import com.example.core.data.community.CommunityRepository
import com.example.core.data.community.CommunityRepositoryImpl
import com.example.core.data.mypage.MyPageRepository
import com.example.core.data.mypage.MyPageRepositoryImpl
import com.example.core.data.token.TokenRepositoryImpl
import com.example.network.interceptor.TokenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindsTokenRepository(
        tokenRepositoryImpl: TokenRepositoryImpl
    ): TokenRepository

    @Binds
    @Singleton
    abstract fun bindsMyPageRepository(
        myPageRepositoryImpl: MyPageRepositoryImpl
    ): MyPageRepository

    @Binds
    @Singleton
    abstract fun bindsCommunityRepository(
        communityRepositoryImpl: CommunityRepositoryImpl
    ): CommunityRepository

    @Binds
    @Singleton
    abstract fun bindsLanguageRepository(
        languageRepositoryImpl: LanguageRepositoryImpl
    ): LanguageRepository
}
