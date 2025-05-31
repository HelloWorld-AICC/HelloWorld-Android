package com.example.hello_world_mvp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HelloWorldApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}