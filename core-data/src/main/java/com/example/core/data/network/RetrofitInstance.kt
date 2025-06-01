package com.example.core.data.network

import com.example.core.data.api.AuthService
import com.example.core.data.api.UserService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private var accessToken: String = ""

    private var retrofit: Retrofit? = null

    fun setAccessToken(token: String) {
        if (accessToken != token) {
            accessToken = token
            retrofit = null
        }
    }

    fun getAccessToken(): String {
        return accessToken
    }

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor()) // accessToken 읽어옴
            .build()
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.gotoend.store/mvc/")
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 항상 최신 토큰 반영을 위해 매번 새 인스턴스 생성
    private fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            retrofit = createRetrofit()
        }

        return retrofit!!
    }

    // AuthService는 로그인 전에만 사용되므로 lazy 사용해도 OK
    val authService: AuthService by lazy {
        getRetrofit().create(AuthService::class.java)
    }

    // UserService는 accessToken이 바뀌면 다시 만들어야 하므로 매번 새로 만들어야 함
    fun userService(): UserService {
        return getRetrofit().create(UserService::class.java)
    }
}
