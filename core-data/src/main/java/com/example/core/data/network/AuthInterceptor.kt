// Retrofit 전역 설정 (모든 API 요청에 자동으로 Authorization 헤더 추가)

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

        val requestBuilder = chain.request().newBuilder()

        // 엑세스 토큰 꺼내서 저장
        val accessToken = RetrofitInstance.getAccessToken()

        //  원래 하려던 요청에 Header() 추가해서 새로운 요청 생성
        if (!accessToken.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", accessToken)
            Log.d("AuthInterceptor", "Authorization 헤더 추가됨: $accessToken")
        } else {
            Log.w("AuthInterceptor", "AccessToken이 비어있어 Authorization 헤더를 추가하지 않음")
        }

        // 새로운 요청을 서버로 전송
        return chain.proceed(requestBuilder.build())
    }
}