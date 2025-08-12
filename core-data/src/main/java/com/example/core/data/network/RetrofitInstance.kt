package com.example.core.data.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.core.data.api.AIChatService
import com.example.core.data.api.AuthService
import com.example.core.data.api.ConsultationCenterService
import com.example.core.data.api.UserService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val BASE_URL = "https://www.gotoend.store/mvc/"
    private const val BASE_URL_WEBFLUX = "https://www.gotoend.store/webflux/"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        Log.d("Prefs", "Stored Token: ${prefs.getString("access_token", "NULL")}")
    }

    fun setAccessToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }

    fun getAccessToken(): String {
        return prefs.getString("access_token", "") ?: ""
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .connectTimeout(60, TimeUnit.SECONDS) // 연결 타임아웃
        .readTimeout(60, TimeUnit.SECONDS)    // 서버 응답 대기 시간
        .writeTimeout(60, TimeUnit.SECONDS)   // 요청 전송 타임아웃
        .build()

    // MVC용 Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // WebFlux용 Retrofit
    private val retrofitWebflux = Retrofit.Builder()
        .baseUrl(BASE_URL_WEBFLUX)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //MVC용 서비스
    val authService: AuthService = retrofit.create(AuthService::class.java)
    val userService: UserService = retrofit.create(UserService::class.java)
    val centerService : ConsultationCenterService = retrofit.create(ConsultationCenterService::class.java)

    //WebFlux용 서비스
    val aiChatService : AIChatService = retrofitWebflux.create(AIChatService::class.java)
}
