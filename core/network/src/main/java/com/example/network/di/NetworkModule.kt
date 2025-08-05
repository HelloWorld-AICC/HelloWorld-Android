package com.example.network.di

import android.content.Context
import com.example.network.common.LanguageApi
import com.example.network.common.LanguageDataSource
import com.example.network.common.RetrofitLanguageDataSource
import com.example.network.community.CommunityApi
import com.example.network.community.CommunityDataSource
import com.example.network.community.RetrofitCommunityDataSource
import com.example.network.interceptor.TokenInterceptor
import com.example.network.mypage.MyPageApi
import com.example.network.mypage.MyPageDataSource
import com.example.network.mypage.RetrofitMyPageDataSource
import com.example.network.retrofit.ApiConstants
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            prettyPrint = false
            isLenient = true
            encodeDefaults = true
        }
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        tokenInterceptor: TokenInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor) // token 자동 추가
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideMyPageApi(retrofit: Retrofit): MyPageApi {
        return retrofit.create(MyPageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMyPageDataSource(
        myPageApi: MyPageApi
    ): MyPageDataSource {
        return RetrofitMyPageDataSource(myPageApi)
    }

    @Provides
    @Singleton
    fun provideCommunityApi(retrofit: Retrofit): CommunityApi {
        return retrofit.create(CommunityApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCommunityDataSource(
        communityApi: CommunityApi,
        @ApplicationContext context: Context,
    ): CommunityDataSource {
        return RetrofitCommunityDataSource(communityApi, context)
    }

    @Provides
    @Singleton
    fun provideLanguageApi(retrofit: Retrofit): LanguageApi {
        return retrofit.create(LanguageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLanguageDataSource(
        languageApi: LanguageApi,
    ): LanguageDataSource {
        return RetrofitLanguageDataSource(languageApi)
    }

}