// 헤더에 토큰 자동 추가 (SharedPreferences 기반)

package com.example.core.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

// Interceptor: 가로채기자
// accessToken: 로그인 성공 후 받은 ATK
class AuthInterceptor : Interceptor {

    // OkHttp가 API 호출 시 intercept() 호출
    // chain: 요청을 계속 이어주는 파이프라인 같은 것..
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = RetrofitInstance.getAccessToken()
        Log.d("AuthInterceptor", "access token: $accessToken")

        val requestBuilder = chain.request().newBuilder()
        if (accessToken.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
            Log.d("AuthInterceptor", "Authorization 헤더 추가됨: Bearer $accessToken")
        } else {
            Log.w("AuthInterceptor", "Authorization 토큰이 비어있습니다.")
        }

        // 새로운 요청을 서버로 전송
        return chain.proceed(requestBuilder.build())
    }
}