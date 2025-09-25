package com.example.core.data.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.core.data.api.AIChatService
import com.example.core.data.api.AuthService
import com.example.core.data.api.ConsultationCenterService
import com.example.core.data.api.UserService
import kotlinx.serialization.json.Json
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val BASE_URL = "https://helloworldhelp.shop/mvc/"
    private const val BASE_URL_WEBFLUX = "https://helloworldhelp.shop/webflux/"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        Log.d("Prefs", "Stored Token: ${prefs.getString("access_token", "NULL")}")
    }

    // 엑세스 토큰 설정
    fun setAccessToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }

    // 엑세스 토큰 조회
    fun getAccessToken(): String {
        return prefs.getString("access_token", "") ?: ""
    }

    // 리프레시 토큰 설정
    fun setRefreshToken(token: String) {
        prefs.edit().putString("refresh_token", token).apply()
    }

    // 리프레시 토큰 조회
    fun getRefreshToken(): String {
        return prefs.getString("refresh_token", "") ?: ""
    }

    // 자동 로그인
    suspend fun tryAutoLogin(): Boolean {
        val rtk = getRefreshToken()
        Log.d("AUTO_LOGIN", "저장된 RTK: $rtk")

        if (rtk.isEmpty()) {
            Log.d("AUTO_LOGIN", "저장된 RTK 없음")
            return false
        }

        return try {
            val response = authService.reissueToken(rtk)

            if (response.isSuccess) {
                val atk = response.result.tokenList.find { it.types.equals("atk", true) }?.token ?: ""
                val newRtk = response.result.tokenList.find { it.types.equals("rtk", true) }?.token ?: rtk

                setAccessToken(atk)
                setRefreshToken(newRtk)

                Log.d("AUTO_LOGIN", "재발급 API 응답 받기 성공")
                true
            } else {
                Log.w("AUTO_LOGIN", "재발급 API 응답 받기 실패")
                false
            }
        } catch (e: Exception) {
            Log.e("AUTO_LOGIN", "자동 로그인 중 오류", e)
            false
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val contentType = "application/json".toMediaType()

    private val json = Json {
        ignoreUnknownKeys = true   // 서버에서 필요 없는 필드 내려와도 무시
        isLenient = true           // json 포맷 조금 느슨하게 허용
        encodeDefaults = true
    }

    // MVC용 Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType)) // ✅ 변경
        .build()

    // WebFlux용 Retrofit
    private val retrofitWebflux = Retrofit.Builder()
        .baseUrl(BASE_URL_WEBFLUX)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType)) // ✅ 변경
        .build()

    // MVC용 서비스
    val authService: AuthService = retrofit.create(AuthService::class.java)
    val userService: UserService = retrofit.create(UserService::class.java)
    val centerService: ConsultationCenterService = retrofit.create(ConsultationCenterService::class.java)

    // WebFlux용 서비스
    val aiChatService: AIChatService = retrofitWebflux.create(AIChatService::class.java)
}
