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
        // 리프레시 토큰 발급받아서 rtk에 저장
        val rtk = getRefreshToken()
        Log.d("AUTO_LOGIN", "저장된 RTK: $rtk") 

        // 리프레시 토큰(rtk) 발급 안됐을 경우
        if (rtk.isEmpty()) {
            Log.d("AUTO_LOGIN", "저장된 RTK 없음")
            return false
        }
        
        /*
        * API 응답 받기 성공 -> true 반환
        * API 응답 받기 실패 -> false 반환
        */
        return try {
            // 발급받은 리프레시 토큰을 재발급 api의 피라미터로 넘기고 응답 받기
            val response = authService.reissueToken(rtk)

            // 응답을 성공적으로 받았을 경우
            if (response.isSuccess) {

                // 타입이 atk일 경우 atk로 저장
                val atk = response.result.tokenList.find { it.types == "atk" }?.token ?: ""

                // 타입이 rtk일 경우 newRtk로 저장
                val newRtk = response.result.tokenList.find { it.types == "rtk" }?.token ?: rtk

                // 재발급 받은 atk로 엑세스 토큰 재설정
                setAccessToken(atk)

                // 재발급 받은 newRtk로 리프레시 토큰 재설정
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
