package com.example.core.data.di

import com.example.core.data.remote.api.UserApi
import com.example.core.data.remote.interceptor.AuthInterceptor
import com.example.core.data.repository.UserRepositoryImpl
import com.example.core.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://your.api.url/"

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor =
        AuthInterceptor { getAccessToken() }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideUserRepository(userApi: UserApi): UserRepository =
        UserRepositoryImpl(userApi)

    // 이건 예시입니다. 실전에서는 TokenManager 같은 클래스를 통해 관리하는 게 좋습니다.
    private fun getAccessToken(): String {
        return "your_oauth_access_token"
    }
}
