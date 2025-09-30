package com.example.hello_world_mvp

import android.app.Application
import com.example.core.data.network.RetrofitInstance
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HelloWorldApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 앱 시작 시 SharedPreferences 초기화
        RetrofitInstance.init(this)
    }
}