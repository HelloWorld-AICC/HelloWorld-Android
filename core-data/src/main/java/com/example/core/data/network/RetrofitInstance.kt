// Retrofit 인스턴스 싱글톤 정의

package com.example.core.data.network
import com.example.core.data.api.AuthService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.gotoend.store/mvc/") // 끝에 / 반드시 필요
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}

